package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>,// 기본적으로 모든 필드에 대한 검색 기능 추가
        QuerydslBinderCustomizer<QArticle> { // 디테일한 검색 기능 추가를 위해 사용 (부분 문자열 검색 등)


    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        bindings.excludeUnlistedProperties(true); // 선택적인 필드들만 검색 기능 동작하게, 다른 필드들은 제외
        bindings.including(root.title,root.content, root.hashtag, root.createdAt, root.createdBy); //원하는 필드 추가
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase); -> like '${value}'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // -> like '%${value}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // TODO : 시분초를 동일하게 넣어줘야 하기 때문에 나중에 바꿔야함
    }
}