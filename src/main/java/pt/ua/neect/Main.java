/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.neect;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
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
        Multiset<String> countAnswers = HashMultiset.<String>create();
        Crawler cr = new NeectCrawler("http://neect.ieeta.pt/forum/viewtopic.php?f=15&t=2487", user, pass);
        while (cr.hasNext()) {
            Document d = cr.next();
            countAnswers.add(d.getAuthor());
           /* if (!d.getAuthor().equals("TiagoNovo")) {
                System.out.println(d.getAuthor() + " : " + d.getContents());
            }*/
        }
        for (String i : Multisets.copyHighestCountFirst(countAnswers).elementSet()) {
            System.out.println(i + " | " + countAnswers.count(i));
        }
    }
    
}
