package org.edu.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.edu.dao.IF_BoardDAO;
import org.edu.service.IF_BoardService;
import org.edu.service.IF_BoardTypeService;
import org.edu.service.IF_MemberService;
import org.edu.util.CommonController;
import org.edu.util.SecurityCode;
import org.edu.vo.AttachVO;
import org.edu.vo.BoardTypeVO;
import org.edu.vo.BoardVO;
import org.edu.vo.MemberVO;
import org.edu.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//스프링에서 사용가능한 클래스를 빈(커피Bean)이라고 하고, @Contorller 클래스를 사용하면 됨.
@Controller
public class AdminController {
	//@Inject == @Autowired 의존성 주입방식 DI(Dependency Inject)으로 
	//외부 라이브러리 = 컴포넌트 = 모듈  = 실행클래스 = 인스턴스 갖다쓰기(아래)
	@Inject
	CommonController commonController;
	
	@Inject
	SecurityCode securityCode;
	
	@Inject
	IF_BoardService boardService;//게시판인터페이스를 주입받아서 boardService오브젝트 생성.
	
	@Inject
	IF_BoardDAO boardDAO;//jsp-Controller-Service-DAO-Mapper-DB
	
	@Inject
	IF_MemberService memberService;//멤버인터페이스를 주입받아서 memberService오브젝트 변수를 생성.
	
	@Inject
	private IF_BoardTypeService boardTypeService;
	
	//게시판생성관리 삭제매핑(POST)
	@RequestMapping(value="/admin/bbs_type/bbs_type_delete",method=RequestMethod.POST)
	public String bbs_type_delete(BoardTypeVO boardTypeVO, RedirectAttributes rdat) throws Exception {
		String board_type = boardTypeVO.getBoard_type();
		PageVO pageVO = new PageVO();
		pageVO.setBoard_type(board_type);
		int board_count = boardService.countBoard(pageVO);
		if(board_count > 0) {
			rdat.addFlashAttribute("msg_fail", "해당게시판의 게시물내용이 존재합니다. 삭제");
			return "redirect:/admin/bbs_type/bbs_type_update?board_type="+board_type;
		}else {
			boardTypeService.delete_board_type(board_type);
			rdat.addFlashAttribute("msg", "삭제");
		}
		return "redirect:/admin/bbs_type/bbs_type_list";
	}
	//게시판생성관리 등록매핑(POST)
	@RequestMapping(value="/admin/bbs_type/bbs_type_write",method=RequestMethod.POST)
	public String bbs_type_wrtie(BoardTypeVO boardTypeVO, RedirectAttributes rdat) throws Exception {
		//메서드명이 같고, 로드된 매개변수가 틀린방식을 오버로드
		boardTypeService.insert_board_type(boardTypeVO);
		rdat.addFlashAttribute("msg", "등록");
		return "redirect:/admin/bbs_type/bbs_type_list";
	}
	
	//게시판생성관리 등록매핑(GET)
	@RequestMapping(value="/admin/bbs_type/bbs_type_write",method=RequestMethod.GET)
	public String bbs_type_write() throws Exception {
		
		return "admin/bbs_type/bbs_type_write";
	}
	
	//게시판생성관리 수정매핑(POST)
	@RequestMapping(value="/admin/bbs_type/bbs_type_update",method=RequestMethod.POST)
	public String bbs_type_update(BoardTypeVO boardTypeVO,RedirectAttributes rdat) throws Exception {
		boardTypeService.update_board_type(boardTypeVO);
		rdat.addFlashAttribute("msg", "수정");
		return "redirect:/admin/bbs_type/bbs_type_update?board_type=" + boardTypeVO.getBoard_type();
	}
	//게시판생성관리 수정매핑(Get)
	@RequestMapping(value="/admin/bbs_type/bbs_type_update",method=RequestMethod.GET)
	public String bbs_type_update(@RequestParam("board_type") String board_type,Model model) throws Exception {
		
		BoardTypeVO boardTypeVO = boardTypeService.view_board_type(board_type);
		model.addAttribute("boardTypeVO", boardTypeVO);
		return "admin/bbs_type/bbs_type_update";
	}
	//게시판생성관리 리스트 매핑
	@RequestMapping(value="/admin/bbs_type/bbs_type_list",method=RequestMethod.GET)
	public String bbs_type_list() throws Exception {
		//여기는 model을 이용해서 jsp로 board_type_list오브젝트를 보낼필요X, ControllAdvice클래스에서 만들었기 때문에...
		return "admin/bbs_type/bbs_type_list";
	}
	//GET은 URL전송방식(아무데서나 브라우저주소에 적으면 실행됨), POST는 폼전송방식(해당페이지에서만 작동가능)
	@RequestMapping(value="/admin/board/board_delete",method=RequestMethod.POST)
	public String board_delete(RedirectAttributes rdat,PageVO pageVO, @RequestParam("bno") Integer bno) throws Exception {
		//기존등록된 첨부파일 폴더에서 삭제할 UUID파일명 구하기(아래)
		List<AttachVO> delFiles = boardService.readAttach(bno);
		//List<HashMap<String,Object>> delFiles_noUse = boardService.readAttach_noUse(bno);
		boardService.deleteBoard(bno);
		//첨부파일 삭제:DB부터 먼저삭제 후 폴더에서 첨부파일 삭제
		for(AttachVO file_name:delFiles) {
			//파일 삭제 로직(아래 File클래스(폴더경로,파일명)
			File target = new File(commonController.getUploadPath(), file_name.getSave_file_name());
			if(target.exists()) {
				target.delete();//실제 파일 지워짐.
			}
		}
		
		rdat.addFlashAttribute("msg", "삭제");
		return "redirect:/admin/board/board_list?page=" + pageVO.getPage();//삭제할 당시의 현재페이지를 가져가서 리스트로보줌
	}
	
	@RequestMapping(value="/admin/board/board_update",method=RequestMethod.GET)
	public String board_update(@RequestParam("bno") Integer bno,@ModelAttribute("pageVO") PageVO pageVO,Model model) throws Exception {
		BoardVO boardVO = boardService.readBoard(bno);
		List<AttachVO> files = boardService.readAttach(bno);
		//List<HashMap<String, Object>> files_noUse = boardService.readAttach_noUse(bno);
		String[] save_file_names = new String[files.size()];
		String[] real_file_names = new String[files.size()];
		int cnt = 0;
		for(AttachVO file_name:files) {//세로데이터를 가로데이터로 변경하는 로직
			save_file_names[cnt] = file_name.getSave_file_name();
			real_file_names[cnt] = file_name.getReal_file_name();
			cnt = cnt + 1;
		}
		
		//배열형출력값(가로) {'save_file_name0','save_file_name1',...}
		boardVO.setSave_file_names(save_file_names);
		boardVO.setReal_file_names(real_file_names);
		//시큐어코딩 시작 적용(아래) jsp에서 c:out jstl로 대체
		//String xss_date = boardVO.getContent();
		//boardVO.setContent(securityCode.unscript(xss_date));
		//시큐어코딩 끝
		model.addAttribute("boardVO", boardVO);
		//model.addAttribute("board_type_list", "게시판타입 리스트 오브젝트");
		//게시판타입리스트는 위처럼 개별 메서드에서 처리하지 않고, AdviceController클래스로 대체 합니다.
		return "admin/board/board_update";//파일경로
	}
	@RequestMapping(value="/admin/board/board_update",method=RequestMethod.POST)
	public String board_update(RedirectAttributes rdat,@RequestParam("file") MultipartFile[] files, BoardVO boardVO, PageVO pageVO) throws Exception {
		//기존 등록된 첨부파일 목록 구하기
		List<AttachVO> delFiles = boardService.readAttach(boardVO.getBno());
		//List<HashMap<String,Object>> delFiles_noUse = boardService.readAttach_noUse(boardVO.getBno());
		//jsp에 보낼 save_file_names, real_file_names 배열변수 초기값 지정
		String[] save_file_names = new String[files.length];
		String[] real_file_names = new String[files.length];
		int index = 0;//아래 향상된 for문에서 사용할 인덱스값 
		//첨부파일 수정: 기존첨부파일 삭제 후 신규파일 업로드
		for(MultipartFile file:files) {//다중파일 업로드 호출 부분 시작 향상된 for문사용
			if(file.getOriginalFilename() != "") {//첨부파일명이 있으면
				
				int sun = 0;//업데이트jsp화면에서 첨부파일을 개별 삭제시 사용할  순서가 필요하기때문 변수 추가
				//기존파일 폴더에서 실제파일 삭제 처리
				for(AttachVO file_name:delFiles) {
					if(index == sun) {//index는 첨부파일개수 , sun삭제할 개별순서
						File target = new File(commonController.getUploadPath(), file_name.getSave_file_name());
						if(target.exists()) {
							target.delete();//폴더에서 기존첨부파일 지우기
							//서비스클래스에는 첨부파일DB를 지우는 메서드가 없음. DAO를 접근해서 tbl_attach를 지웁니다.
							boardDAO.deleteAttach(file_name.getSave_file_name());
						}
					}
					sun = sun + 1;//개별삭제는 for문에서 딱 1번 뿐이기 때문에
				}
				//신규파일 폴더에 업로드 처리
				save_file_names[index] = commonController.fileUpload(file);//신규파일 폴더에 업로드
				real_file_names[index] = file.getOriginalFilename();//신규파일 한글파일명 저장
			}else{
				save_file_names[index] = null;//신규파일 폴더에 업로드
				real_file_names[index] = null;//신규파일 한글파일명 저장
			}
			index = index + 1; 
		}
		boardVO.setSave_file_names(save_file_names);//UUID로 생성된 유니크한 파일명
		boardVO.setReal_file_names(real_file_names);
		boardService.updateBoard(boardVO);//DB에서 업데이트
		rdat.addFlashAttribute("msg", "수정");
		return "redirect:/admin/board/board_view?page="+pageVO.getPage()+"&bno="+boardVO.getBno();
	}
	
	@RequestMapping(value="/admin/board/board_write",method=RequestMethod.GET)//URL경로
	public String board_write() throws Exception {
		return "admin/board/board_write";//파일경로
	}
	@RequestMapping(value="/admin/board/board_write",method=RequestMethod.POST)
	public String board_write(RedirectAttributes rdat,@RequestParam("file") MultipartFile[] files, BoardVO boardVO) throws Exception {
		//post받은 boardVO내용을 DB서비스에 입력하면 됩니다.
		//dB에 입력후 새로고침명령으로 게시물 테러를 당하지 않으려면, redirect로 이동처리 합니다.(아래)
		String[] save_file_names = new String[files.length];//배열크기가 존재하는 변수 생성 
		String[] real_file_names = new String[files.length];
		int index = 0;
		//첨부파일이 있으면, 첨부파일 업로드처리 후 게시판DB저장+첨부파일DB저장
		for(MultipartFile file:files) {
			if(file.getOriginalFilename() != "") {//첨부파일명이 있으면
				save_file_names[index] = commonController.fileUpload(file);//폴더에 업로드저장완료
				real_file_names[index] = file.getOriginalFilename();//"한글파일명.jpg"
			}
			index = index + 1;//배열 인덱스 변수값 증가처리.
		}
		boardVO.setSave_file_names(save_file_names);//UUID로 생성된 유니크한 파일명
		boardVO.setReal_file_names(real_file_names);
		boardService.insertBoard(boardVO);
		
		rdat.addFlashAttribute("msg", "저장");
		return "redirect:/admin/board/board_list";
	}
	
	@RequestMapping(value="/admin/board/board_view", method=RequestMethod.GET)
	public String board_view(@ModelAttribute("pageVO") PageVO pageVO, @RequestParam("bno") Integer bno, Model model) throws Exception {
		
		BoardVO boardVO = boardService.readBoard(bno);
		//시큐어코딩 시작
		String xss_data = boardVO.getContent();
		boardVO.setContent(securityCode.unscript(xss_data));
		//시큐어코딩 끝
		//첨부파일 리스트 값을 가져와서 세로데이터(jsp에서는 forEach문사용)를 가로데이터(jsp에서 배열사용)로 바꾸기
		//첨부파일을 1개만 올리기 때문에 리스트형 데이터를 배열데이터로 변경
		// 리스트형 입력값(세로) [
		// {'save_file_name0'},
		// {'save_file_name1'},
		// ..
		//]
		List<AttachVO> files = boardService.readAttach(bno);
		//List<HashMap<String, Object>> files_noUse = boardService.readAttach_noUse(bno);
		String[] save_file_names = new String[files.size()];
		String[] real_file_names = new String[files.size()];
		int cnt = 0;
		for(AttachVO file_name:files) {//세로데이터를 가로데이터로 변경하는 로직
			save_file_names[cnt] = file_name.getSave_file_name();
			real_file_names[cnt] = file_name.getReal_file_name();
			cnt = cnt + 1;
		}
		
		//배열형출력값(가로) {'save_file_name0','save_file_name1',...}
		boardVO.setSave_file_names(save_file_names);
		boardVO.setReal_file_names(real_file_names);
		//위처럼 첨부파일을 세로베치->가로배치로 바꾸고, get/set하는 이유는 attachVO를 만들지 않아서 입니다.
		//만약 위처럼 복잡하게 세로배치->가로배치로 바꾸는 것이 이상하면, 아래처럼처리
		//model.addAttribute("save_file_names", files);
		model.addAttribute("boardVO", boardVO);
		model.addAttribute("checkImgArray", commonController.getCheckImgArray());
		return "admin/board/board_view";
	}
	
	@RequestMapping(value="/admin/board/board_list",method=RequestMethod.GET)
	public String board_list(@ModelAttribute("pageVO") PageVO pageVO, Model model) throws Exception {
		
		// selectBoard마이바티스쿼리를 실행하기전에 set이 발생해야 변수값이 할당됩니다.(아래)
		// PageVO의 queryStartNo구하는 계산식 먼저 실행되어서 변수값이 발생되어야 합니다.
		if(pageVO.getPage() == null) {//int 일때 null체크에러가 나와서 pageVO의 page변수형 Integer로벼경.
			pageVO.setPage(1);
		}
		pageVO.setPerPageNum(8);//리스트하단에 보이는 페이징번호의 개수
		pageVO.setQueryPerPageNum(10);//쿼리에서 1페이지당 보여줄 게시물수 10개로 입력 놓았습니다.
		//검색된 전체 게시물수 구하기 서비스 호출
		int countBoard = 0;
		countBoard = boardService.countBoard(pageVO);
		pageVO.setTotalCount(countBoard);//11x개 전체 게시물 수를 구한 변수 값 매개변수로 입력하는 순간 calcPage()메서드실행.
		
		List<BoardVO> board_list = boardService.selectBoard(pageVO);
		model.addAttribute("board_list", board_list);
		//model.addAttribute("pageVO", pageVO);//@ModelAttribute 애노테이션으로 대체
		return "admin/board/board_list";
	}
	
	//메서드 오버로딩(예, 동영상 로딩중..., 로딩된 매개변수가 다르면, 메서드이름을 중복가능합니다. 대표적인 다형성구현)
	@RequestMapping(value="/admin/member/member_write",method=RequestMethod.POST)
	public String member_write(HttpServletRequest request, MultipartFile file, @Valid MemberVO memberVO) throws Exception {
		if(file.getOriginalFilename() != null) {
			commonController.profile_upload(memberVO.getUser_id(), request, file);
		}
		//아래 GET방식의 폼 출력화면에서 데이터 전송받은 내용을 처리하는 바인딩.
		//POST방식으로 넘어온 user_pw값을 BCryptPasswordEncoder클래스로 암호시킴
		if(memberVO.getUser_pw() != null) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String userPwEncoder = passwordEncoder.encode(memberVO.getUser_pw());
			memberVO.setUser_pw(userPwEncoder);
		}
		//DB베이스 입력/출력/삭제/수정 처리-다음에...
		memberService.insertMember(memberVO);
		return "redirect:/admin/member/member_list";//절대경로로 처리된 이후에 이동할 URL주소를 여기에 반환
	}
	
	@RequestMapping(value="/admin/member/member_write",method=RequestMethod.GET)
	public String member_write() throws Exception {
		return "admin/member/member_write";
	}
	
	@RequestMapping(value="/admin/member/member_update",method=RequestMethod.GET)
	public String member_update(@RequestParam("user_id") String user_id, @ModelAttribute("pageVO") PageVO pageVO, Model model) throws Exception {
		//GET방식으로 업데이트 폼파일만 보여줍니다.
		MemberVO memberVO = memberService.readMember(user_id);
		model.addAttribute("memberVO", memberVO);
		return "admin/member/member_update";
	}
	//첨부파일처리는 MultipartFile(첨부파일 태그name 1개일때) , MultipartServletRequest(첨부파일 태그name이 여러개일때) 
	@RequestMapping(value="/admin/member/member_update",method=RequestMethod.POST)
	public String member_update(HttpServletRequest request,MultipartFile file,PageVO pageVO,@Valid MemberVO memberVO) throws Exception {
		//프로필 첨부파일 처리
		if(file.getOriginalFilename() != null) {
			commonController.profile_upload(memberVO.getUser_id(), request, file);
		}
		//POST방식으로 넘어온 user_pw값을 BCryptPasswordEncoder클래스로 암호시킴
		//if(memberVO.getUser_pw() == null || memberVO.getUser_pw() == "") {
		if(memberVO.getUser_pw().isEmpty()) {
		} else {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String userPwEncoder = passwordEncoder.encode(memberVO.getUser_pw());
			memberVO.setUser_pw(userPwEncoder);
		}
		//POST방식으로 넘어온 값을 DB수정처리하는 역할
		memberService.updateMember(memberVO);
		//redirect를 사용하는 목적은 새로고침 했을때, 위 updateMember메서드를 재 실행방지 목적입니다.
		return "redirect:/admin/member/member_view?page="+pageVO.getPage()+"&user_id=" + memberVO.getUser_id();
	}
	
	@RequestMapping(value="/admin/member/member_delete",method=RequestMethod.POST)
	public String member_delete(RedirectAttributes rdat, @RequestParam("user_id") String user_id) throws Exception {
		memberService.deleteMember(user_id);
		//Redirect로 페이지 이동시 전송값을 숨겨서 보내는 역할 클래스 RedirctAttributes 입니다.
		rdat.addFlashAttribute("msg", "삭제");
		return "redirect:/admin/member/member_list";//?success=ok
	}
	
	@RequestMapping(value="/admin/member/member_view",method=RequestMethod.GET)
	public String member_view(@ModelAttribute("pageVO") PageVO pageVO, @RequestParam("user_id") String user_id, Model model) throws Exception {
		
		MemberVO memberVO = memberService.readMember(user_id);
		model.addAttribute("memberVO", memberVO);
		//model.addAttribute("user_id2", user_id + "<script>alert('메롱');</script> 님");
		return "admin/member/member_view";
	}
	
	@RequestMapping(value="/admin/member/member_list",method=RequestMethod.GET)
	public String member_list(@ModelAttribute("pageVO") PageVO pageVO, Model model) throws Exception {
		
		// selectMember마이바티스쿼리를 실행하기전에 set이 발생해야 변수값이 할당됩니다.(아래)
		if(pageVO.getPage() == null) {//int 일때 null체크에러가 나와서 pageVO의 page변수형 Integer로벼경.
			pageVO.setPage(1);
		}
		pageVO.setPerPageNum(8);//리스트하단에 보이는 페이징번호의 개수
		pageVO.setQueryPerPageNum(10);//쿼리에서 1페이지당 보여줄 회원수 10명으로 입력 놓았습니다.
		//검색된 전체 회원 명수 구하기 서비스 호출
		int countMember = 0;
		countMember = memberService.countMember(pageVO);
		pageVO.setTotalCount(countMember);//115전체 회원의 수를 구한 변수 값 매개변수로 입력하는 순간 calcPage()메서드실행.
		
		List<MemberVO> members_list = memberService.selectMember(pageVO);
		model.addAttribute("members", members_list);//members-2차원배열을 members_array클래스오브젝트로 변경
		
		return "admin/member/member_list";//member_list.jsp 로 members변수명으로 데이터를 전송
	}
	
	//bind:묶는다는 의미, /admin 요청URL경로와 admin/home.jsp를 묶는다는 의미.
	@RequestMapping(value="/admin",method=RequestMethod.GET)
	public String admin(Model model) throws Exception {
		//대시보드 만들기 1번 방법: ModelMap<key:objcet>값을 만들어서 보내기
		PageVO pageVO = new PageVO();
		pageVO.setPage(1);
		pageVO.setPerPageNum(5);
		pageVO.setQueryPerPageNum(4);
		List<MemberVO> latest_member = memberService.selectMember(pageVO);
		model.addAttribute("latest_member", latest_member);

		return "admin/home";//상대경로 파일위치
	}
	//관리자단 대시보드에 나타낼 다중게시판 최근게시물 출력하는 바인딩
	@RequestMapping(value="/admin/latest/latest_board",method=RequestMethod.GET)
	public String latest_board(@RequestParam("board_type") String board_type,Model model) throws Exception {
		PageVO pageVO = new PageVO();
		pageVO.setBoard_type(board_type);//jsp > import jstl로 ?~쿼리스트링으로 받은 변수값
		pageVO.setPage(1);
		pageVO.setPerPageNum(5);
		pageVO.setQueryPerPageNum(5);
		List<BoardVO> latest_list = boardService.selectBoard(pageVO);
		model.addAttribute("latest_list", latest_list);
		
		return "admin/latest/latest_board";
	}
	
}