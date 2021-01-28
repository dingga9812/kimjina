package org.edu.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.edu.vo.BoardTypeVO;
import org.springframework.stereotype.Repository;

@Repository
public class BoardTypeDAOImpl implements IF_BoardTypeDAO {
	@Inject
	private SqlSession sqlSession;
	
	@Override
	public List<BoardTypeVO> select_board_type() throws Exception {
		// 게시판 타입 리스트를 출력하는 쿼리 매핑(아래)
		return sqlSession.selectList("boardTypeMapper.selectBoardType");
	}

	@Override
	public BoardTypeVO view_board_type(String board_type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update_board_type(BoardTypeVO boardTypeVO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert_board_type(BoardTypeVO boardTypeVO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete_board_type(String boardTypeVO) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
