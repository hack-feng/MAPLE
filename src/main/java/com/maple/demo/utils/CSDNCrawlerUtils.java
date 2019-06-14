package com.maple.demo.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSDNCrawlerUtils {

    //查询文章条数计数器
    private static volatile  int COUNT = 0;
    //默认查询条数
    private static volatile int MAX = 10;
    //默认查询阅读量超过的条数
    private static volatile int REND_NUM = 100;

    /**
     * 获取https://blog.csdn.net/qq_34988304地址下，我的博客信息
     * @param about
     * @param num
     * @param readNum
     * @return
     */
    public static List<Map<String, Object>> csdn_crawler(String about, Integer num, Integer readNum){
        MAX = num;
        List<Map<String, Object>> list = new ArrayList<>();
        String url="https://blog.csdn.net/qq_34988304";
        Document doc = getUrl(url);

        //这里根据在网页中分析的类选择器来获取列表所在的节点
        Elements table = doc.getElementsByClass("article-list");
        Element tbs = table.get(0);
        Elements lists = tbs.getElementsByClass("article-item-box");
        //获取选中节点的列表总数
        int result=lists.size();
        System.out.println("列表数为:"+result);
        for (Element tb : lists) {
            try {
                Thread.sleep(1000);//让线程操作不要太快 1秒一次 时间自己设置，主要是模拟人在点击
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //获取所有详情的链接所在的节点
            Elements h4 = tb.select("h4");
            //获取文章访问链接
            String urls = h4.get(0).select("a").attr("href");
            //访问文章，获取文章内容
            Document contetnDoc = getUrl(urls);
            Map<String, Object> map = getContent(contetnDoc);
            list.add(map);
        }
        for(Map flag : list){
            System.out.println(flag);
        }
        return list;
    }

    /**
     * 根据关键词，获取阅读量大于readNum的num条相关博客
     * 首先遍历搜索页的列表数据获取文章url，然后根据url访问文章内容，获取readNum符合条件的文章
     * 然后获取访问的文章内容的相关文件的链接，获取url，然后依次访问。
     * 由此循环，直至获取文章的数量符合num的数据为止。
     * @param about
     * @param num
     * @param readNum
     * @return
     */
    public static List<Map<String, Object>> csdn_about(String about, Integer num, Integer readNum){
        List<Map<String, Object>> list = new ArrayList<>();
        String url = "https://so.csdn.net/so/search/s.do?q=a"+about;

        //定义存放搜索页的url地址
        Set<String> urlSet = new HashSet<>();

        Document doc = getUrl(url);

        Elements elements = doc.getElementsByClass("search-list-con");
        Elements eleList = elements.get(0).getElementsByTag("dt");

        for (Element flag : eleList){
            //获取文章类型
            String title = flag.getElementsByTag("span").get(0).text();
            String urls = flag.select("a").get(0).attr("href");
            if ("博客".equals(title)) {
                urlSet.add(urls);
            }
        }
        if(urlSet.size() > 0){
            list = getArticleList(urlSet);
        }


        for(Map flag : list){
            System.out.println(flag);
        }

        return list;
    }

    /**
     * 获取文章列表
     * @return
     */
    public static List<Map<String, Object>> getArticleList(Set<String> set){
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> urlSet = new HashSet<>();
        for (String url : set){
            Document doc = getUrl(url);
            Map<String, Object> contentMap = getContent(doc);
            if(contentMap != null){
                if(COUNT < MAX){
                    result.add(contentMap);
                    COUNT++;
                    urlSet.addAll(getContentUrls(doc));
                }else {
                    break;
                }
            }
        }
        getArticleList(urlSet);
        return result;
    }

    /**
     * 获取博客的详细内容
     * @param doc
     * @return
     */
    public static Map<String, Object> getContent(Document doc){
        Map<String, Object> map = new HashMap<>();
        String content = null;
        int read = 0;
        String title = null;
        String auth = null;
        if(doc != null){
            // 获取标题
            title = doc.getElementsByClass("article-title-box").get(0).text();
            Element all = doc.getElementsByClass("article-info-box").get(0);
            // 获取作者
            auth = doc.select("a").text();

            //获取阅读量
            String num = all.getElementsByClass("read-count").get(0).text();
            num = num.replace("阅读数 ","");
            if(StringUtils.isNotEmpty(num)){
                read = Integer.valueOf(num);
            }

            //获取内容
            Elements elements = doc.getElementsByAttributeValue("id","content_views");
            Elements flag = elements.first().getElementsByTag("svg");
            flag.remove();
            Element element = elements.get(0);
            content = element.toString();
        }

        map.put("read", read);
        map.put("title", title);
        map.put("auth", auth);
        map.put("content", content);
        return map;
    }


    /**
     * 获取文章详情下的文章列表信息
     * @param doc
     * @return
     */
    public static Set<String> getContentUrls(Document doc){
        Set<String> urls = new HashSet<>();
        Elements els = doc.getElementsByClass("recommend-item-box");
        for (Element flag : els){
            try {
                //如果a节点为空或不存在，则跳出本次循环
                if(flag.select("a") == null || flag.select("a").size() == 0){
                    continue;
                }
                Element el = flag.select("a").get(0);
                String url = el.attr("href");
                String title = el.attr("title");

                if(flag.getElementsByClass("read-num") == null || flag.getElementsByClass("read-num").size() == 0){
                    continue;
                }
                String num = flag.getElementsByClass("read-num").get(0).text();

                String regEx = "[^0-9]";
                Pattern pattern = Pattern.compile(regEx);
                Matcher m = pattern.matcher(num);

                Integer numFlag = Integer.valueOf(m.replaceAll(""));

                if(numFlag != null && numFlag > 0){
                    urls.add(url);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
        }

        return urls;

    }




    /**
     * 模拟火狐浏览器请求
     * @param url
     * @return
     */
    public static Document getUrl(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();//模拟火狐浏览器
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
