package com.freeletics.pullrequestcommentor.collector.model.xml;

import com.freeletics.pullrequestcommentor.collector.Comment;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ElementNameMatcher;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;
import java.util.Objects;

@Xml(name = "comments")
public class XmlComments {

    private final List<XmlComment> comments;

    public XmlComments(
            @Element(
                    typesByElement = {
                            @ElementNameMatcher(type = XmlFileLineComment.class),
                            @ElementNameMatcher(type = XmlSimpleComment.class)
                    }
            )
                    List<XmlComment> comments) {
        this.comments = comments;
    }

    public List<XmlComment> getComments() {
        return comments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlComments)) return false;
        XmlComments that = (XmlComments) o;
        return Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {

        return Objects.hash(comments);
    }

    @Override
    public String toString() {
        return "XmlComments{" +
                "comments=" + comments +
                '}';
    }
}
