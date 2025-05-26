package com.TTT.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.BandRecruitPostVo;
import com.TTT.mapper.BandInsertMapper;
import com.TTT.mapper.BandRecruitPostMapper;

// 밴드 결성 후 밴드 상세 조회
@Service
public class BandRecruitPostService {

	@Autowired
	private BandRecruitPostMapper bandRecruitpostMapper;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지 저장 경로
																																				

	// username을 기반으로 user_id(UUID) 조회
	public String findUserIdByUsername(String username) {
		return bandRecruitpostMapper.findUserIdByUsername(username);
	}

	// 구인구직 insert
	public Long insertRecruitPostWithTags(BandRecruitPostVo vo, MultipartFile[] imageFiles, String selectedGenres,
			String selectedPositions, String selectedGenders, String selectedAges) {

		vo.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		// null 또는 비어있을 경우 대비
		if (imageFiles != null && imageFiles.length > 0) {
			for (int i = 0; i < imageFiles.length && i < 4; i++) {
				MultipartFile file = imageFiles[i];
				if (file != null && !file.isEmpty()) {
					try {
						String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
						Path filePath = Paths.get(uploadDir + fileName);
						Files.copy(file.getInputStream(), filePath);

						switch (i) {
						case 0:
							vo.setImage1Url(fileName);
							break;
						case 1:
							vo.setImage2Url(fileName);
							break;
						case 2:
							vo.setImage3Url(fileName);
							break;
						case 3:
							vo.setImage4Url(fileName);
							break;
						}
					} catch (IOException e) {
						throw new RuntimeException("이미지 저장 실패", e);
					}
				}
			}
		}

		// ✅ post insert
		bandRecruitpostMapper.insertBandRecruitPost(vo);
		Long postId = vo.getPostId(); // auto_increment PK 받기

		// 태그 저장
		String[] types = { "genre", "position", "gender", "age" };
		String[] values = { selectedGenres, selectedPositions, selectedGenders, selectedAges };

		for (int i = 0; i < types.length; i++) {
			if (values[i] == null || values[i].isBlank())
				continue;

			for (String tag : values[i].split(",")) {
				if (tag.trim().isEmpty())
					continue;

				BandRecruitPostVo tagVo = new BandRecruitPostVo();
				tagVo.setPostId(postId);
				tagVo.setTag_type(types[i]);
				tagVo.setTag_value(tag.trim());

				bandRecruitpostMapper.insertRecruitTag(tagVo);
			}
		}

		return postId;
	}

	// band_id 호출
	public List<Long> findBandIdsByUserId(String userId) {
		return bandRecruitpostMapper.findBandIdsByUserId(userId);
	}

	// 밴드 리스트 전체 조회 (리더용)
	public List<BandRecruitPostVo> getMyBandList(String userId) {
		return bandRecruitpostMapper.getBandsByUserId(userId);
	}

	// 리스트 조회
	public List<BandRecruitPostVo> getAllRecruitPosts() {
		return bandRecruitpostMapper.selectAllRecruitPosts();
	}

	// 전체 게시글 수 조회
	public int getTotalPostCount() {
	    return bandRecruitpostMapper.countRecruitPosts();
	}

	// 페이징된 리스트 조회
	public List<BandRecruitPostVo> getRecruitPostsByPage(int page, int size) {
	    int offset = Math.max(0, (page - 1) * size);
	    return bandRecruitpostMapper.getRecruitPostsByPage(size, offset);
	}

	
	

}
