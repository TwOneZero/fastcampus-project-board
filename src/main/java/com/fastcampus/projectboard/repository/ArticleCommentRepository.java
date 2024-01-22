package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>, // 기본적으로 모든 필드에 대한 검색 기능 추가
        QuerydslBinderCustomizer<QArticleComment> { // 디테일한 검색 기능 추가를 위해 사용 (부분 문자열 검색 등)


    List<ArticleComment> findByArticle_Id(Long articleId); //Spring data jpa 공식문서 보기

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true); // 선택적인 필드들만 검색 기능 동작하게, 다른 필드들은 제외
        bindings.including( root.content, root.createdAt, root.createdBy); //원하는 필드 추가
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // -> like '%${value}%'
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // TODO : 시분초를 동일하게 넣어줘야 하기 때문에 나중에 바꿔야함
    }
}