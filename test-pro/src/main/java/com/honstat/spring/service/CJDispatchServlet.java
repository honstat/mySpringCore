package com.honstat.spring.service;

import com.alibaba.fastjson.JSONObject;
import com.honstat.spring.annotaiton.JCAutoWrited;
import com.honstat.spring.annotaiton.JCController;
import com.honstat.spring.annotaiton.JCRequestMapping;
import com.honstat.spring.annotaiton.JCService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service
 * @Description: 处理请求
 * @date 2019/8/13 13:40
 */
public class CJDispatchServlet extends HttpServlet {
    private Properties properties = new Properties();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> ioc = new HashMap<>();
    private Map<String, Method> urlMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatcherServlet(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void init(ServletConfig config) {

        //加载配置
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");

        loadConfig(contextConfigLocation);
        //获取要扫描的包地址
        String dirpath = properties.getProperty("scanner.package");

        //扫描要加载的类
        doScanner(dirpath);
        //实例化要加载的类

        doInstance();

        //加载依赖注入，给属性赋值
        doAutoWirted();

        //加载映射地址
        doRequestMapping();
    }

    void loadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getJson(HttpServletRequest req) {
        String param = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            param = responseStrBuilder.toString();
            System.out.println(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    void doDispatcherServlet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        url = url.replace(req.getContextPath(), "").replaceAll("/+", "/");
        if (!urlMapping.containsKey(url)) {
            resp.getWriter().write("404! url is not found!");
            return;
        }

        Method method = urlMapping.get(url);
        String className = method.getDeclaringClass().getSimpleName();
        className = firstLowerCase(className);
        if (!ioc.containsKey(className)) {
            resp.getWriter().write("500! claas not defind !");
            return;
        }
        Object[] args = null;
        if ("POST".equals(req.getMethod()) && req.getContentType().contains("json")) {
            String str = getJson(req);
            args = getRequestParam(str, method);
        } else {
            args = getRequestParam(req.getParameterMap(), method);
        }

        Object res = method.invoke(ioc.get(className), args);
        resp.getWriter().write(res.toString());
    }

    Object[] getRequestParam(String json, Method method) {
        if (null == json || json.isEmpty()) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        Object[] requestParam = new Object[parameters.length];
        JSONObject jsonObject = JSONObject.parseObject(json);
        int i = 0;
        for (Parameter p : parameters) {
            Object val = jsonObject.getObject(p.getName(), p.getType());
            requestParam[i] = val;
            i++;
        }
        return requestParam;
    }

    Object[] getRequestParam(Map<String, String[]> map, Method method) {
        if (null == map || map.size() == 0) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        int i = 0;
        Object[] requestParam = new Object[parameters.length];
        for (Parameter p : parameters) {
            if (!map.containsKey(p.getName())) {
                requestParam[i] = null;
                i++;
                continue;
            }
            try {
                Class typeClass = p.getType();
                String[] val = map.get(p.getName());
                if (null == val) {
                    requestParam[i] = null;
                    i++;
                    continue;
                }
                Constructor con = null;
                try {
                    con = typeClass.getConstructor(val[0].getClass());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                Object obj = null;
                try {
                    obj = con.newInstance(val);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                requestParam[i] = obj;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            i++;
        }
        return requestParam;
    }

    void doRequestMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> obj : ioc.entrySet()) {
            if (!obj.getValue().getClass().isAnnotationPresent(JCController.class)) {
                continue;
            }
            Method[] methods = obj.getValue().getClass().getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(JCRequestMapping.class)) {
                    continue;
                }
                String baseUrl = "";
                if (obj.getValue().getClass().isAnnotationPresent(JCRequestMapping.class)) {
                    baseUrl = obj.getValue().getClass().getAnnotation(JCRequestMapping.class).value();
                }
                JCRequestMapping jcRequestMapping = method.getAnnotation(JCRequestMapping.class);
                if ("".equals(jcRequestMapping.value())) {
                    continue;
                }
                String url = (baseUrl + "/" + jcRequestMapping.value()).replaceAll("/+", "/");
                urlMapping.put(url, method);
                System.out.println(url);
            }
        }
    }

    void doAutoWirted() {
        for (Map.Entry<String, Object> obj : ioc.entrySet()) {
            try {
                for (Field field : obj.getValue().getClass().getDeclaredFields()) {
                    if (!field.isAnnotationPresent(JCAutoWrited.class)) {
                        continue;
                    }
                    JCAutoWrited autoWrited = field.getAnnotation(JCAutoWrited.class);
                    String beanName = autoWrited.value();
                    if ("".equals(beanName)) {
                        beanName = field.getType().getSimpleName();
                    }

                    field.setAccessible(true);

                    field.set(obj.getValue(), ioc.get(firstLowerCase(beanName)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }

    void doInstance() {
        if (beanNames.isEmpty()) {
            return;
        }
        for (String beanName : beanNames) {
            try {
                Class cls = Class.forName(beanName);
                if (cls.isAnnotationPresent(JCController.class)) {
                    //使用反射实例化对象
                    Object instance = cls.newInstance();
                    //默认类名首字母小写
                    beanName = firstLowerCase(cls.getSimpleName());
                    //写入ioc容器
                    ioc.put(beanName, instance);


                } else if (cls.isAnnotationPresent(JCService.class)) {
                    Object instance = cls.newInstance();
                    JCService jcService = (JCService) cls.getAnnotation(JCService.class);

                    String alisName = jcService.value();
                    if (null == alisName || alisName.trim().length() == 0) {
                        beanName = cls.getSimpleName();
                    } else {
                        beanName = alisName;
                    }
                    beanName = firstLowerCase(beanName);
                    ioc.put(beanName, instance);
                    //如果是接口，自动注入它的实现类
                    Class<?>[] interfaces = cls.getInterfaces();
                    for (Class<?> c :
                            interfaces) {
                        ioc.put(firstLowerCase(c.getSimpleName()), instance);
                    }
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    void doScanner(String dirpath) {
        URL url = this.getClass().getClassLoader().getResource("/" + dirpath.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                doScanner(dirpath + "." + file.getName());
                continue;
            }

            //取文件名
            String beanName = dirpath + "." + file.getName().replaceAll(".class", "");
            beanNames.add(beanName);
        }
    }

    String firstLowerCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
