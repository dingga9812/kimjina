package org.edu.aop;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.edu.vo.BoardVO;
import org.edu.vo.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * DebugAdvice클래스로서 디버그를 Advice라는 AOP기능을 사용해서 디버그를 실행하게 됩니다.
 * @author 김진아
 *
 */
@Component //스프링빈으로 사용하겠다는 명시
@Aspect //AOP기능을 사용하겠다는 명시 
public class DebugAdvice {
	private static final Logger logger = LoggerFactory.getLogger(DebugAdvice.class);
	
	@Around("execution(* org.edu.service.MemberService*.*(..))")
	//@Around("execution(* org.edu.controller.AdminController.*(..))")//컨트롤러의 메서드는 실행안됨
	public Object timeLog(ProceedingJoinPoint pjp) throws Throwable {
		logger.info("AOP 디버그 시작=========================");
		long startTime = System.currentTimeMillis();//현재 컴퓨터시간을 저장하는 변수
		logger.info(Arrays.toString(pjp.getArgs()));//pjp클래스 매개변수 값 GET으로 가져와서 toString형변환 출력
		//위는 현재 시간체크하는 메서드가 어떤메서드인지 눈으로 확인하려고 logger.debug로 출력
		Object result = pjp.proceed();//AdminController에 있는 메서드가 실행됩니다.(시간이 소요됨)
		long endTime = System.currentTimeMillis();//현재 컴퓨터 시간을 저장하는 변수
		logger.info(pjp.getSignature().getName() + "()메서드명 의 실행시간은:" + (double)(endTime-startTime)/1000 + "초 입니다.");
		logger.info("AOP 디버그 끝 ==========================");
		return result;
	}
	
	
	@Around("execution(* org.edu.controller.*Controller.*(..))")
	public Object sessionManager(ProceedingJoinPoint pjp) throws Throwable {
		//AOP에서 RequestContextHolder클래스를 이용해서 HttpServletRequest 오브젝트를 사용하기(아래)
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		//컨트롤러 클래스에서 매개변수로 받는 값 초기화
		PageVO pageVO = null;
		BoardVO boardVO = null;
		String board_type = null;
		//컨트롤러 클래스에서 실행되는 모든 메서드=*Controller.*(..) 의 매개변수 값을 꺼내오기(아래) 향상된 for문사용
		for(Object object:pjp.getArgs()) {//예, board_update()메서드의 매개변수Arguments
			logger.info("jsp를 통해서 호출된 컨트롤러의 메서드 매개변수 꺼내오기: " + object);
			if(object instanceof PageVO) {//instanceof 객체타입를 비교하는 연산자
				pageVO = (PageVO) object;
				board_type = pageVO.getBoard_type();//세션변수로 사용할 값을 발생.jsp에서 발생한 notice,gallery값
			}else if(object instanceof BoardVO) {
				boardVO = (BoardVO) object;
			}
		}
		
		if(request != null) {//jsp에서 요청사항이 발생될때만 실행(아래)
			HttpSession session = request.getSession();
			if(board_type != null) {//최초로 세션발생
				session.setAttribute("session_board_type", board_type);
			}
			//PageVO와 BoardVO에서 세션변수로 get/set 하기 때문에 
			if(session.getAttribute("session_board_type") != null ) {
				board_type = (String) session.getAttribute("session_board_type");
			}
			if(pageVO != null) {
				pageVO.setBoard_type(board_type);//다중게시판 검색쿼리때문에 추가
			}
			if(boardVO != null) {
				boardVO.setBoard_type(board_type);//다중게시판 인서트+업데이트 때문에 추가
			}
		}
		
		Object result = pjp.proceed();
		return result;
	}
	
}
