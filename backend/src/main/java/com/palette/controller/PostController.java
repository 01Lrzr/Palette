package com.palette.controller;

import com.palette.domain.member.Member;
import com.palette.domain.post.MyFile;
import com.palette.domain.post.Post;
import com.palette.domain.post.PostGroup;
import com.palette.dto.GeneralResponse;
import com.palette.dto.SearchCondition;
import com.palette.dto.request.PostRequestDto;
import com.palette.dto.response.PostResponseDto;
import com.palette.dto.response.StoryListResponseDto;
import com.palette.service.PostGroupService;
import com.palette.service.PostService;
import com.palette.utils.ConstantUtil;
import com.palette.utils.HttpResponseUtil;
import com.palette.utils.S3Uploader;
import com.palette.utils.annotation.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostGroupService postGroupService;
    private final S3Uploader s3Uploader;

    // 그룹을 안거치고 조회시, 그룹 거친 조회는 PostGroup에 존재
    @GetMapping("/post")
    public ResponseEntity<GeneralResponse> getPosts(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String region,
                                                    @RequestParam(required = false) String title,
                                                    @RequestParam(defaultValue = ConstantUtil.DEFAULT_PAGE,required = false) int page){
        SearchCondition searchCondition = setSearchCondition(name, region, title);
        List<StoryListResponseDto> storyList = postService.findStoryList(searchCondition, page);
        GeneralResponse<Object> res = GeneralResponse.builder().data(storyList).build();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/post/{id}")
    public PostResponseDto getSinglePost(@PathVariable Long id){
        PostResponseDto postResponseDto = postService.findSinglePost(id, ConstantUtil.INIT_ID);
        return postResponseDto;
    }

    @PostMapping("/postgroup/{postGroupId}/post")
    public Long createPost(@Login Member member, @PathVariable Long postGroupId, @RequestPart("data")@Valid PostRequestDto postRequestDto, @RequestPart("files") List<MultipartFile> imageFiles) throws IOException {
        PostGroup findPostGroup = postGroupService.findById(postGroupId);
        postService.isAvailablePostOnPostGroup(findPostGroup, member.getId());
        List<MyFile> myFiles = s3Uploader.uploadFiles(imageFiles);
        Post post = Post.builder().title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .member(member)
                .region(findPostGroup.getRegion())
                .period(findPostGroup.getPeriod())
                .build();
        Post savedPost = postService.write(post, findPostGroup, myFiles);
        return savedPost.getId();
    }

    @PutMapping("/postgroup/{postGroupId}/post/{id}")
    public ResponseEntity<Void> updatePost(@Login Member member, @PathVariable("postGroupId") Long postGroupId, @PathVariable("id") Long postId, @RequestBody @Valid PostRequestDto postRequestDto){
        validateMemberCanUpdateOrDeletePost(member, postGroupId, postId);
        postService.update(postId,postRequestDto);
        return HttpResponseUtil.RESPONSE_OK;
    }

    @DeleteMapping("/postgroup/{postGroupId}/post/{id}")
    public ResponseEntity<Void> deletePost(@Login Member member, @PathVariable("postGroupId") Long postGroupId, @PathVariable("id") Long postId, @RequestBody @Valid PostRequestDto postRequestDto){
        validateMemberCanUpdateOrDeletePost(member, postGroupId, postId);
        postService.delete(postId);
        return HttpResponseUtil.RESPONSE_OK;
    }

    private void validateMemberCanUpdateOrDeletePost(Member member, Long postGroupId, Long postId) {
        PostGroup findPostGroup = postGroupService.findById(postGroupId);
        Post findPost = postService.findById(postId);
        postService.isAvailablePostOnPostGroup(findPostGroup, member.getId());
        postService.isAvailableUpdatePost(findPost, member);
    }


    private SearchCondition setSearchCondition(String name, String region, String title) {
        SearchCondition searchCondition = new SearchCondition();
        if(name != null){
            log.info("검색 조건 {} 추가", name);
            searchCondition.setName(name);
        }
        if(region != null){
            log.info("검색 조건 {} 추가", region);
            searchCondition.setRegion(region);
        }
        if(title != null){
            log.info("검색 조건 {} 추가", title);
            searchCondition.setTitle(title);
        }
        return searchCondition;
    }


}
