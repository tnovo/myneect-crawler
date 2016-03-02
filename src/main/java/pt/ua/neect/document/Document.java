/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.neect.document;

import java.io.IOException;
import java.time.LocalTime;

/**
 *
 * @author Tiago Novo <tmnovo at ua.pt>
 */
public class Document implements Appendable {

    private final String post_id;
    private final String author;
    private final StringBuffer contents;
    private final LocalTime time;

    public Document(String post_id, String author, String contents,
                    LocalTime time) {
        this.author = author;
        this.contents = new StringBuffer(contents);
        this.time = time;
        this.post_id = post_id;
    }

    public Document(String post_id, String author, LocalTime time) {
        this(post_id, author, "", time);
    }

    public String getPost_id() {
        return post_id;
    }

    @Override
    public Appendable append(char c) throws IOException {
        contents.append(c);
        return this;
    }

    @Override
    public Appendable append(CharSequence cs) throws IOException {
        contents.append(cs);
        return this;
    }

    @Override
    public Appendable append(CharSequence cs, int i, int i1) throws IOException {
        contents.append(cs, i, i1);
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public String getContents() {
        return contents.toString();
    }

    public LocalTime getTime() {
        return time;
    }

}
