package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("testdb")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) -> application.yml 에 지정가능
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) //내가 만든 JpaConfig를 import (Auditing 기능 위해)
@DataJpaTest // 모든 단위 테스트들이 각 트랜잭션으로 동작하게 돼서 자동으로 rollback됨
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        //Given

        //When
        List<Article> articles = articleRepository.findAll();


        //Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        //Given
        long prevCntOfArticles = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(
                UserAccount.of("twonezero", "asdf1234", null, null, null)
        );
        Article article = Article.of(userAccount, "new Article", "new content", "#spring");

        //When
        articleRepository.save(article);

        //Then
        assertThat(articleRepository.count())
                .isEqualTo(prevCntOfArticles + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        //When
        Article savedArticle = articleRepository.save(article);

        //Then
        assertThat(savedArticle)
                .hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        //Given
        Article article = articleRepository.findById(1L).orElseThrow();

        long articlesCountBeforeDeleting = articleRepository.count();
        long articlesCommentsCountBeforeDeleting = articleCommentRepository.count();
        //게시물에 있던 댓글 수
        int deletedCommentSize = article.getArticleComments().size();

        //When
        articleRepository.delete(article);

        //Then
        assertThat(articleRepository.count())
                .isEqualTo(articlesCountBeforeDeleting - 1);
        assertThat(articleCommentRepository.count())
                .isEqualTo(articlesCommentsCountBeforeDeleting - deletedCommentSize);

    }

}