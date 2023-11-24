package com.fastcampus.projectboard.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


// @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false/*,cascade = none -> default*/)
    private Article article; //게시글 (ID)


    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 내용

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일자
    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy; // 생성자
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일자
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

    protected ArticleComment(){}

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ArticleComment that = (ArticleComment) o;
        if(!(o instanceof ArticleComment articleComment)) return false;
        // id != null -> 아직 영속화되지 않은 객체는 같지 않음
        return id != null && id.equals(articleComment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
