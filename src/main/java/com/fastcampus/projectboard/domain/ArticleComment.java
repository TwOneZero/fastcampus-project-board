package com.fastcampus.projectboard.domain;

import java.time.LocalDateTime;

public class ArticleComment {
    private long id;
    private Article article; //게시글 (ID)

    private String content; // 내용
    private LocalDateTime createdAt; // 생성일자
    private String createdBy; // 생성자
    private LocalDateTime modifiedAt; // 수정일자
    private String modifiedBy; // 수정자
}
