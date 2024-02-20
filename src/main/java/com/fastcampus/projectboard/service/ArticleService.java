package com.fastcampus.projectboard.service;


import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.Hashtag;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import com.fastcampus.projectboard.repository.HashtagRepository;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final HashtagService hashtagService;
    private final UserAccountRepository userAccountRepository;
    private final ArticleRepository articleRepository;
    private final HashtagRepository hashtagRepository;


    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
//      SearchType ->  TITLE, CONTENT, ID, NICKNAME, HASHTAG
        return switch (searchType) { // java 11 ver 이상 switch 문법
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(
                            Arrays.stream(searchKeyword.split(" ")).toList(),
                            pageable
                    )
                    .map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }



    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        //해시태그 본문에서 파싱
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());

        //아티클과 해시태그 따로 저장
        Article article = dto.toEntity(userAccount);
        article.addHashtags(hashtags);
        articleRepository.save(article);
    }

    public void updateArticle(Long articleId,ArticleDto dto) {
        try{
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if(article.getUserAccount().equals(userAccount)){ // 유저아아디로 업데이트 인증
                // title , content, hashtag
                if(dto.title() != null){article.setTitle(dto.title());}
                if(dto.content() != null){article.setContent(dto.content());}

                //해시태그 업데이트 로직
                Set<Long> hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(Collectors.toUnmodifiableSet());

                article.clearHashtags();
                articleRepository.flush();
                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

                Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
                article.addHashtags(hashtags);
                //
            }
        }catch (EntityNotFoundException e){
            log.warn("게시글 업데이트 실패. 필요한 정보를 찾을 수 없습니다. - {}",e.getLocalizedMessage());
        }

    }

    public void deleteArticle(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        Set<Long> hashtagIds = article.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());
        //article 삭제
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
        articleRepository.flush();

        //해시태그 삭제
        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

    }


    public long getArticleCount() {return articleRepository.count();}

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable) {
        if(hashtagName ==null || hashtagName.isBlank()){
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable)
                .map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return hashtagRepository.findAllHashtagNames(); //TODO : 해시태그서비스로 이동 고려
    }

    private Set<Hashtag> renewHashtagsFromContent(String content) {
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);

        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        hashtagNamesInContent.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return Collections.unmodifiableSet(hashtags);
    }

}
