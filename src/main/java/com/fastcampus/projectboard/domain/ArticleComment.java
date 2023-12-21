package com.fastcampus.projectboard.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;


// @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(callSuper = true) //AuditingFields 에 있는 것도 ToString 가능하게
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false/*,cascade = none -> default*/)
    private Article article; //게시글 (ID)

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 내용


    protected ArticleComment() {
    }

    private ArticleComment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return new ArticleComment(article, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ArticleComment that = (ArticleComment) o;
        if (!(o instanceof ArticleComment articleComment)) return false;
        // id != null -> 아직 영속화되지 않은 객체는 같지 않음
        return id != null && id.equals(articleComment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
