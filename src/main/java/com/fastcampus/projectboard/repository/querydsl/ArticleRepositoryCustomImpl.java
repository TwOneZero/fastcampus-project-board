package com.fastcampus.projectboard.repository.querydsl;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {
//    /**
//     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
//     *
//     * @param domainClass must not be {@literal null}.
//     */
//    public ArticleRepositoryCustomImpl(Class<?> domainClass) {
//        super(domainClass);
//    }
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

//        JPQLQuery<String> query = from(article)
//                .distinct().select(article.hashtag)
//                .where(article.hashtag.isNotNull());
//
//        return query.fetch();

        return from(article)
                .distinct().select(article.hashtag)
                .where(article.hashtag.isNotNull())
                .fetch();
    }
}
