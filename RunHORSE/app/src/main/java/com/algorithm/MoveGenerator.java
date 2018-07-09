package com.algorithm;

// 走发产生器
public class MoveGenerator{
	
	public Chessmov[][] m_MoveList = new Chessmov[8][80];//存放每一层的所有走法
	public int m_nMoveCount;// 记录m_MoveList中走法的数量
	
	public MoveGenerator(){
		super();
	}

	public boolean IsValidMove(int position[][], int nFromX, int nFromY, int nToX, int nToY){
		if (nToX < 0 || nFromX < 0) {// 当左边出界时
			return false;
		}
		if (nToX > 8 || nFromX > 8) {// 当右边出界时
			return false;
		}
		if (nToY < 0 || nFromY < 0) {// 当上边出界时
			return false;
		}
		if (nToY > 9 || nFromY > 9) {// 当下边出界时
			return false;
		}
		int i = 0, j = 0;
		int nMoveChessID, nTargetID;
		if (nFromY ==  nToY && nFromX == nToX)
			return false;//目的与源相同
		nMoveChessID = position[nFromY][nFromX];
		nTargetID = position[nToY][nToX];
		if (Chessutil.isSameSide(nMoveChessID, nTargetID) || nTargetID == Chessconst.B_CAR)
			return false;//不能吃自己的棋
		
		switch(nMoveChessID) 
		{
			case Chessconst.B_HORSE:   // 黑马
			case Chessconst.R_HORSE:   // 红马

				if(!((Math.abs(nToX-nFromX)==1 && Math.abs(nToY-nFromY)==2)
						||(Math.abs(nToX-nFromX)==2&& Math.abs(nToY-nFromY)==1)))
					return false;//马走日字
				if	(nToX-nFromX==2)
				{
					i=nFromX+1;
					j=nFromY;
				}
				else if	(nFromX-nToX==2)
				{
					i=nFromX-1;
					j=nFromY;
				}
				else if	(nToY-nFromY==2)
				{
					i=nFromX;
					j=nFromY+1;
				}
				else if	(nFromY-nToY==2)
				{
					i=nFromX;
					j=nFromY-1;
				}
				if(position[j][i] != Chessconst.NOCHESS)
					return false; //绊马腿
				break;

			case Chessconst.B_PAWN:     //黑兵
				if(nToY < nFromY)
					return false;//兵不回头
				if( nFromY < 5 && nFromY == nToY)
					return false;//兵过河前只能直走
				if(nToY - nFromY + Math.abs(nToX - nFromX) > 1)
					return false;//兵只走一步直线:
				break;

			case Chessconst.R_PAWN:    //红兵
				if(nToY > nFromY)
					return false;//兵不回头
				if( nFromY > 4 && nFromY == nToY)
					return false;//兵过河前只能直走
				if(nFromY - nToY + Math.abs(nToX - nFromX) > 1)
					return false;//兵只走一步直线:
				break;


		    case Chessconst.B_KING:   // 黑帅
		    case Chessconst.R_KING: // 红将
		    case Chessconst.R_BISHOP:  // 红士
		    case Chessconst.B_BISHOP:   //黑士
		    case Chessconst.R_ELEPHANT: //红象
		    case Chessconst.B_ELEPHANT://黑象
		    case Chessconst.B_CAR:  // 黑车
		    case Chessconst.R_CAR:  // 红车
		    case Chessconst.B_CANON: // 黑炮
		    case Chessconst.R_CANON: // 红炮
                return false;
		    default:
			    return false;
		}
		return true;
	}

	/**
	 * 
	 * @param position 当前棋子的二维数组
	 * @param nPly 当前搜索的层数 ， 每层将走法存在不同的位置
	 * @param nSide 指明产生哪一方的走法，1为红方，0为黑方
	 * @return
	 */
	
	public int CreatePossibleMove(int position[][], int nPly, int nSide){
		int	nChessID;
		int	i,j;
		
		m_nMoveCount = 0;
		
		for (i = 0; i < 10; i++)
			for (j = 0; j < 9; j++)
			{
				nChessID = position[i][j];
				if (nChessID == Chessconst.B_HORSE || nChessID == Chessconst.B_PAWN || nChessID == Chessconst.R_HORSE
						|| nChessID == Chessconst.R_PAWN)
				{
					if (nSide == 0 && Chessutil.isRed(nChessID))
						continue;// 如果产生黑棋走法，跳过红棋
					if (nSide != 0 && Chessutil.isBlack(nChessID))
						continue;//如果产生红棋走法，跳过黑棋
					switch(nChessID)
					{
                        case Chessconst.R_PAWN://红卒
                            Gen_RPawnMove(position, i, j, nPly);
                            break;
                        case Chessconst.B_PAWN://红卒
                            Gen_BPawnMove(position, i, j, nPly);
                            break;
                        case Chessconst.R_HORSE: //红马
                        case Chessconst.B_HORSE: //黑马
                            Gen_HorseMove(position, i, j, nPly);
                            break;
						
					    default:
						    break;
						
					}// end of switch
				}
			}
			
		return m_nMoveCount; // 返回总的走法数目
	}

    /**
     * 马，8个方向
     */

    public void Gen_HorseMove(int[][] position, int i, int j, int nPly) {
        int x,  y;

        x=j+2;
        y=i+1;
        if((x < 9 && y < 10) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        x=j+2;
        y=i-1;
        if((x < 9 && y >= 0) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        x=j-2;
        y=i+1;
        if((x >= 0 && y < 10) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        x=j-2;
        y=i-1;
        if((x >= 0 && y >= 0) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        x=j+1;
        y=i+2;
        if((x < 9 && y < 10) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
        x=j-1;
        y=i+2;
        if((x >= 0 && y < 10) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
        x=j+1;
        y=i-2;
        if((x < 9 && y >= 0) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
        x=j-1;
        y=i-2;
        if((x >= 0 && y >= 0) &&IsValidMove(position, j, i, x, y))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
    }

    /**
     * 红卒
     */

    public void Gen_RPawnMove(int[][] position, int i, int j, int nPly) {
        int x, y;
        int nChessID;

        nChessID = position[i][j];

        y = i - 1;//向前
        x = j;
        if(y > 0 && !Chessutil.isSameSide(nChessID, position[y][x]))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        if(i < 5) //是否已经过河
        {
            y=i;
            x=j+1;
            if(x < 9 && !Chessutil.isSameSide(nChessID, position[y][x]))
                AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
            x=j-1;
            if(x >= 0 && !Chessutil.isSameSide(nChessID, position[y][x]))
                AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
        }

    }

    /**
     * 黑兵
     */
    public void Gen_BPawnMove(int[][] position, int i, int j, int nPly) {
        int x, y;
        int nChessID;

        nChessID = position[i][j];

        y = i + 1;
        x = j;
        if(y < 10 && !Chessutil.isSameSide(nChessID, position[y][x]))
            AddMove(position[i][j],position[y][x],j, i, x, y, nPly);

        if(i > 4)
        {
            y=i;
            x=j+1;
            if(x < 9 && !Chessutil.isSameSide(nChessID, position[y][x]))
                AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
            x=j-1;
            if(x >= 0 && !Chessutil.isSameSide(nChessID, position[y][x]))
                AddMove(position[i][j],position[y][x],j, i, x, y, nPly);
        }
    }


	
	public int AddMove(int chessId,int chessId2 ,int nFromX, int nFromY,int nToX, int nToY, int nPly) {
		m_MoveList[nPly][m_nMoveCount] = new Chessmov();
		m_MoveList[nPly][m_nMoveCount].chessId = chessId;
		m_MoveList[nPly][m_nMoveCount].chessId2 = chessId2;
		m_MoveList[nPly][m_nMoveCount].From = new Chessmanpos();
		m_MoveList[nPly][m_nMoveCount].From.x = nFromX;
		m_MoveList[nPly][m_nMoveCount].From.y = nFromY;
		m_MoveList[nPly][m_nMoveCount].To = new Chessmanpos();
		m_MoveList[nPly][m_nMoveCount].To.x = nToX;
		m_MoveList[nPly][m_nMoveCount].To.y = nToY;
		m_nMoveCount++;
		return m_nMoveCount;
	}
	
}
