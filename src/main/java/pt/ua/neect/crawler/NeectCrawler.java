/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.neect.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pt.ua.neect.document.Document;

public class NeectCrawler implements Crawler {

    private final Map<String, String> cookies;

    public final static DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm");
    private final Queue<Document> posts;
    private int page;
    private final int forum;
    private final int topic;

    public NeectCrawler(String url, String user, String pass) throws IOException {

        this.cookies = getCookies(user, pass);
        Map<String, String> query = splitQuery(new URL(url));
        this.forum = Integer.parseInt(query.get("f"));
        this.topic = Integer.parseInt(query.get("t"));

        this.posts = new LinkedList<>();
        this.page = 0;

    }

    public static Map<String, String> splitQuery(URL url) throws
            UnsupportedEncodingException {
        Map<String, String> query_pairs = new HashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx
                                                                                                                 + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Override
    public boolean hasNext() {
        return !posts.isEmpty() || hasNextPage();
    }

    @Override
    public Document next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (posts.isEmpty()) {
            page++;
            try {
                org.jsoup.nodes.Document acPage = Jsoup.connect(getUrl(page)).cookies(cookies).get();
                Elements postsElements = acPage.getElementsByClass("post");
                for (Element post : postsElements) {

                    String contents = post.getElementsByClass("content").first().text();
                    Element au = post.getElementsByClass("author").first();

                    String author = au.getElementsByTag("strong").first().getElementsByTag("a").text();
                    String text = au.text();
                    text = text.substring(text.indexOf("»")+1).trim();

                    LocalTime date = LocalTime.parse(text, dtformatter);
                    String post_id = post.id();
                    Document d = new Document(post_id, author, contents, date);
                    posts.offer(d);
                }
            } catch (IOException ex) {
                Logger.getLogger(NeectCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return posts.poll();
    }

    private Map<String, String> getCookies(String user, String pass) throws
            IOException {
        String url = "http://neect.ieeta.pt/member/login";
        Connection.Response loginPage = Jsoup.connect(url).method(Method.GET).execute();
        String csrf_token = loginPage.parse().getElementById("login__csrf_token").val();
        Connection.Response res = Jsoup.connect(url)
                .data("login[username]", user, "login[password]", pass, "login[_csrf_token]", csrf_token)
                .cookies(loginPage.cookies())
                .method(Method.POST)
                .execute();
      //  System.out.println(csrf_token);
      //  System.out.println(res.parse().getElementsByTag("form"));
        return res.cookies();
    }

    private String getUrl(int page) {
        StringBuffer url = new StringBuffer("http://neect.ieeta.pt/forum/viewtopic.php?f=")
                .append(forum)
                .append("&t=").append(topic);
        if (page > 1) {
            url.append("&start=").append((page - 1) * 10);
        }
        
        return url.toString();

    }

    private boolean hasNextPage() {
        try {

            org.jsoup.nodes.Document firstPage = Jsoup.connect(getUrl(1)).cookies(cookies).get();

            //System.out.println(firstPage);
            String text = firstPage.getElementsByClass("pagination").first().text();
            Scanner sctext = new Scanner(text.trim());
            int nPosts = sctext.nextInt();
            return (nPosts / 10) + 1 > page;
        } catch (IOException ex) {
            return false;
        }
    }

}
