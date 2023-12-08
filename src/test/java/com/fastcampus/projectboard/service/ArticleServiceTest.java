package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 게시판")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    /*
     * SpringBoot나 Unit Test에 해당하는 Dependency 를 사용하지 않기 때문에 Application Context 가 뜨지 않고
     * 필요한 의존성은 Mocking 하는 방식으로 가벼운 테스트를 실행함.
     * */
    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;


    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환")
    @Test
    void givenSearchParams_whenSearchingArticles_thenReturnsArticleList() {
        //Given

        //When
        Page<ArticleDto> articles =  articleService.searchArticles(/*Enum type*/SearchType.TITLE, "search keyword example"); // 제목, 본문, id, 닉네임, 해시태그
        //Then

        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 요청 -> 게시글 반환")
    @Test
    void givenId_whenSearchingArticle_thenReturnsArticle() {
        //Given

        //When
        ArticleDto article =  articleService.searchArticle(1L); // 제목, 본문, id, 닉네임, 해시태그
        //Then

        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 정보 입력 -> 게시글 생성")
    @Test
    void givenArticleInfo_whenSubmit_thenSaveArticle() {
        //Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        articleService.saveArticle(ArticleDto.of(LocalDateTime.now(),"Twonezero","새 글","새 글 입력 테스트","#새글"));

        //Then -> save를 구현하지 않아서 save 에러가 나야함
        /* Error 내용
          Wanted but not invoked:
          articleRepository.save(
              <any com.fastcampus.projectboard.domain.Article>
          );
          */
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 ID와 수정정보 입력 -> 게시글 수정")
    @Test
    void givenIdAndModifyingInfo_whenSubmit_thenUpdatesArticle() {
        //Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        //When
        articleService.updateArticle(1L, ArticleUpdateDto.of("수정된 제목","수정한 내용","#수정"));

        //Then -> save를 구현하지 않아서 save 에러가 나야함
        /* Error 내용
         * Wanted but not invoked:
         * articleRepository.save(
         *     <any com.fastcampus.projectboard.domain.Article>
         * );
         * */
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 ID 입력 -> 게시글 삭제")
    @Test
    void givenId_whenSubmit_thenDeletesArticle() {
        //Given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        //When
        articleService.deleteArticle(1L);

        //Then

        then(articleRepository).should().delete(any(Article.class));
    }


}
