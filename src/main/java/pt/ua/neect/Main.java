/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.neect;

import java.io.IOException;
import pt.ua.neect.crawler.Crawler;
import pt.ua.neect.crawler.NeectCrawler;
import pt.ua.neect.document.Document;

/**
 *
 * @author Tiago Novo <tmnovo at ua.pt>
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String user = args[0];
        String pass = args[1];
       
        Crawler cr = new NeectCrawler("http://neect.ieeta.pt/forum/viewtopic.php?f=12&t=2534", user, pass);
        while (cr.hasNext()) {
            Document d = cr.next();
            System.out.println(d.getAuthor() + " : " + d.getContents());
        }
    }

}
