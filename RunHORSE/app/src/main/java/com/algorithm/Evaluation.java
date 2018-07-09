package com.algorithm;

public class Evaluation {
	
	// 定义每种棋子的基本价值
	final static int BASEVALUE_PAWN = 100;
	final static int BASEVALUE_BISHOP = 0;
	final static int BASEVALUE_ELEPHANT = 0;
	final static int BASEVALUE_CAR = 0;
	final static int BASEVALUE_HORSE = -10;
	final static int BASEVALUE_CANON = 0;
	final static int BASEVALUE_KING = 10000;
	
	// 定义各棋子的灵活性 ,每多一个可走位置应加上的分值
	final static int FLEXIBILITY_PAWN = 5;
	final static int FLEXIBILITY_BISHOP = 0;
	final static int FLEXIBILITY_ELEPHANT = 0;
	final static int FLEXIBILITY_CAR = 0;
	final static int FLEXIBILITY_HORSE = 0;
	final static int FLEXIBILITY_CANON = 0;
	final static int FLEXIBILITY_KING = 0;
	
	//红卒的附加值矩阵
	final int BA0[][]= new int[][]
	{
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{90,90,110,120,120,120,110,90,90},
		{90,90,110,120,120,120,110,90,90},
		{70,90,110,110,110,110,110,90,70},
		{70,70,70, 70, 70,  70, 70,70,70},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
	};
	
	//黑兵的附加值矩阵
	final int[][] BA1 = new int[][]
	{
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0},
		{70,70,70, 70, 70,70, 70,70, 70},
		{70,90,110,110,110,110,110,90,70},
		{90,90,110,120,120,120,110,90,90},
		{90,90,110,120,120,120,110,90,90},
		{0,  0, 0,  0,  0,  0,  0,  0,  0},
	};
	
	/**
	 * 为每一个兵返回附加值
	 * @param x
	 * @param y
	 * @param CurSituation
	 * @return
	 */
	int GetBingValue(int x, int y, int CurSituation[][])
	{
		if (CurSituation[y][x] == Chessconst.R_PAWN)
			return BA0[y][x];
		
		if (CurSituation[y][x] == Chessconst.B_PAWN)
			return BA1[y][x];
		return 0;
	}
	
	int m_BaseValue[]= new int[15];//存放棋子基本价值的数组
	int m_FlexValue[]= new int[15];//存放棋子灵活性分数的数组
	int m_AttackPos[][]= new int[10][9];//存放每一位置被威胁的信息
	int m_GuardPos[][]= new int[10][9];//存放每一位置被保护的信息
	int m_FlexibilityPos[][]= new int[10][9];//存放每一位置上的棋子的灵活性分数
	int m_chessValue[][] =new int[10][9];//存放每一位置上的棋子的总价值
	int nPosCount = 0;//记录一棋子的相关位置个数
	Chessmanpos RelatePos[]= new Chessmanpos[20];//记录一棋子相关位子的数组
	
	public Evaluation() {
		m_BaseValue[Chessconst.B_KING] = BASEVALUE_KING; 
		m_BaseValue[Chessconst.B_CAR] = BASEVALUE_CAR; 
		m_BaseValue[Chessconst.B_HORSE] = BASEVALUE_HORSE; 
		m_BaseValue[Chessconst.B_BISHOP] = BASEVALUE_BISHOP; 
		m_BaseValue[Chessconst.B_ELEPHANT] = BASEVALUE_ELEPHANT; 
		m_BaseValue[Chessconst.B_CANON] = BASEVALUE_CANON; 
		m_BaseValue[Chessconst.B_PAWN] = BASEVALUE_PAWN; 

		m_BaseValue[Chessconst.R_KING] = BASEVALUE_KING; 
		m_BaseValue[Chessconst.R_CAR] = BASEVALUE_CAR; 
		m_BaseValue[Chessconst.R_HORSE] = BASEVALUE_HORSE; 
		m_BaseValue[Chessconst.R_BISHOP] = BASEVALUE_BISHOP; 
		m_BaseValue[Chessconst.R_ELEPHANT] = BASEVALUE_ELEPHANT; 
		m_BaseValue[Chessconst.R_CANON] = BASEVALUE_CANON; 
		m_BaseValue[Chessconst.R_PAWN] = BASEVALUE_PAWN; 

		m_FlexValue[Chessconst.B_KING] = FLEXIBILITY_KING; 
		m_FlexValue[Chessconst.B_CAR] = FLEXIBILITY_CAR; 
		m_FlexValue[Chessconst.B_HORSE] = FLEXIBILITY_HORSE; 
		m_FlexValue[Chessconst.B_BISHOP] = FLEXIBILITY_BISHOP; 
		m_FlexValue[Chessconst.B_ELEPHANT] = FLEXIBILITY_ELEPHANT; 
		m_FlexValue[Chessconst.B_CANON] = FLEXIBILITY_CANON; 
		m_FlexValue[Chessconst.B_PAWN] = FLEXIBILITY_PAWN; 

		m_FlexValue[Chessconst.R_KING] = FLEXIBILITY_KING; 
		m_FlexValue[Chessconst.R_CAR] = FLEXIBILITY_CAR; 
		m_FlexValue[Chessconst.R_HORSE] = FLEXIBILITY_HORSE; 
		m_FlexValue[Chessconst.R_BISHOP] = FLEXIBILITY_BISHOP; 
		m_FlexValue[Chessconst.R_ELEPHANT] = FLEXIBILITY_ELEPHANT; 
		m_FlexValue[Chessconst.R_CANON] = FLEXIBILITY_CANON; 
		m_FlexValue[Chessconst.R_PAWN] = FLEXIBILITY_PAWN; 
	}
	
	public int count = 0 ;//全局变量
	/**
	 * 
	 * @param position
	 * @param bIsRedTurn 轮到谁的标志，!0 是红,0 是黑
	 * @return
	 */
	public int Eveluate(int position[][], int bIsRedTurn){
		int i, j, k;
		int nChessType, nTargetType;
		count ++;

		for(i=0;i<10;i++){ // 初始化数值
			for(j=0;j<9;j++){
				m_chessValue[i][j] = 0 ;
				m_AttackPos[i][j] = 0 ;
				m_GuardPos[i][j] = 0 ;
				m_FlexibilityPos[i][j] = 0;
			}
		}
		
		for(i = 0; i < 10; i++)
			for(j = 0; j < 9; j++)
			{
				if(position[i][j] != Chessconst.NOCHESS)
				{
					nChessType = position[i][j];
					GetRelatePiece(position, j, i);
					for (k = 0; k < nPosCount; k++)
					{
						nTargetType = position[RelatePos[k].y][RelatePos[k].x];
						if (nTargetType == Chessconst.NOCHESS)
						{
							m_FlexibilityPos[i][j]++;	
						}
						else
						{
							if (Chessutil.isSameSide(nChessType, nTargetType))
							{
								m_GuardPos[RelatePos[k].y][RelatePos[k].x]++;
							}else
							{
								m_AttackPos[RelatePos[k].y][RelatePos[k].x]++;
								m_FlexibilityPos[i][j]++;	
								switch (nTargetType)
								{
								case Chessconst.R_KING:
									if (bIsRedTurn == 0)
										return 18888;
									break;
								case Chessconst.B_KING:
									if (bIsRedTurn == 1)
										return 18888;
									break;
								default:
									m_AttackPos[RelatePos[k].y][RelatePos[k].x] += (30 + (m_BaseValue[nTargetType] - m_BaseValue[nChessType])/10)/10;
									break;
								}
							}
						}
					}
				}
			}

		for(i = 0; i < 10; i++)
			for(j = 0; j < 9; j++)
			{
				if(position[i][j] != Chessconst.NOCHESS)
				{
					nChessType = position[i][j];
					m_chessValue[i][j]++;
					m_chessValue[i][j] += m_FlexValue[nChessType] * m_FlexibilityPos[i][j];
					m_chessValue[i][j] += GetBingValue(j, i, position);
				}
			}
		int nHalfvalue;
		for(i = 0; i < 10; i++)
			for(j = 0; j < 9; j++)
			{
				if(position[i][j] != Chessconst.NOCHESS)
				{
					nChessType = position[i][j];
					nHalfvalue = m_BaseValue[nChessType]/16;
					m_chessValue[i][j] += m_BaseValue[nChessType];
					
					if (Chessutil.isRed(nChessType))
					{
						if (m_AttackPos[i][j] != 0)
						{
							if (bIsRedTurn != 0 )
							{
								if (nChessType == Chessconst.R_KING)
								{
									m_chessValue[i][j]-= 20;
								}
								else
								{
									m_chessValue[i][j] -= nHalfvalue * 2;
									if (m_GuardPos[i][j] != 0)
										m_chessValue[i][j] += nHalfvalue;
								}
							}
							else
							{
								if (nChessType == Chessconst.R_KING)
									return 18888;
								m_chessValue[i][j] -= nHalfvalue*10;
								if (m_GuardPos[i][j] != 0)
									m_chessValue[i][j] += nHalfvalue*9;
							}
							m_chessValue[i][j] -= m_AttackPos[i][j];
						}
						else
						{
							if (m_GuardPos[i][j] != 0)
								m_chessValue[i][j] += 5;
						}
					}
					else
					{
						if (m_AttackPos[i][j] != 0)
						{
							if (bIsRedTurn == 0)
							{
								if (nChessType == Chessconst.B_KING)									
								{
									m_chessValue[i][j]-= 20;
								}
								else
								{
									m_chessValue[i][j] -= nHalfvalue * 2;
									if (m_GuardPos[i][j] != 0)
										m_chessValue[i][j] += nHalfvalue;
								}
							}
							else
							{
								if (nChessType == Chessconst.B_KING)
									return 18888;
								m_chessValue[i][j] -= nHalfvalue*10;
								if (m_GuardPos[i][j] != 0)
									m_chessValue[i][j] += nHalfvalue*9;
							}
							m_chessValue[i][j] -= m_AttackPos[i][j];
						}
						else
						{
							if (m_GuardPos[i][j] != 0)
								m_chessValue[i][j] += 5;
						}
					}
				}
			}

		int nRedValue = 0; 
		int	nBlackValue = 0;

		for(i = 0; i < 10; i++)
			for(j = 0; j < 9; j++)
			{
				nChessType = position[i][j];
//				if (nChessType == R_KING || nChessType == B_KING)
//					m_chessValue[i][j] = 10000;	
				if (nChessType != Chessconst.NOCHESS)
				{
					if (Chessutil.isRed(nChessType))
					{
						nRedValue += m_chessValue[i][j];	
					}
					else
					{
						nBlackValue += m_chessValue[i][j];	
					}
				}
			}
			if (bIsRedTurn != 0)
			{
				return nRedValue - nBlackValue;
			}
			else
			{
				return  nBlackValue-nRedValue ;
			}
	}
	
	/**
	 * 枚举给定位置上的所有相关位置
	 * @param position
	 * @param j
	 * @param i
	 * @return
	 */
	int GetRelatePiece(int position[][],int j, int i){
		nPosCount = 0;
		int nChessID;
		boolean flag;
		int x,y;
		
		nChessID = position[i][j];
		switch(nChessID)
		{
//		case Chessconst.R_KING:
//		case Chessconst.B_KING:
//
//			for (y = 0; y < 3; y++)
//				for (x = 3; x < 6; x++)
//					if (CanTouch(position, j, i, x, y))
//						AddPoint(x, y);
//			for (y = 7; y < 10; y++)
//				for (x = 3; x < 6; x++)
//					if (CanTouch(position, j, i, x, y))
//						AddPoint(x, y);
//			break;
//
//		case Chessconst.R_BISHOP:
//
//			for (y = 7; y < 10; y++)
//				for (x = 3; x < 6; x++)
//					if (CanTouch(position, j, i, x, y))
//						AddPoint(x, y);
//			break;
//
//		case Chessconst.B_BISHOP:
//
//			for (y = 0; y < 3; y++)
//				for (x = 3; x < 6; x++)
//					if (CanTouch(position, j, i, x, y))
//						AddPoint(x, y);
//			break;
//
//		case Chessconst.R_ELEPHANT:
//		case Chessconst.B_ELEPHANT:
//
//			x=j+2;
//			y=i+2;
//			if(x < 9 && y < 10  && CanTouch(position, j, i, x, y))
//				AddPoint(x, y);
//
//			x=j+2;
//			y=i-2;
//			if(x < 9 && y>=0  &&  CanTouch(position, j, i, x, y))
//				AddPoint(x, y);
//
//			x=j-2;
//			y=i+2;
//			if(x>=0 && y < 10  && CanTouch(position, j, i, x, y))
//				AddPoint(x, y);
//
//			x=j-2;
//			y=i-2;
//			if(x>=0 && y>=0  && CanTouch(position, j, i, x, y))
//				AddPoint(x, y);
//			break;
//
			case Chessconst.R_HORSE:		
			case Chessconst.B_HORSE:		
				x=j+2;
				y=i+1;
				if((x < 9 && y < 10) &&CanTouch(position, j, i, x, y))
					AddPoint(x, y);
						
						x=j+2;
						y=i-1;
						if((x < 9 && y >= 0) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						
						x=j-2;
						y=i+1;
						if((x >= 0 && y < 10) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						
						x=j-2;
						y=i-1;
						if((x >= 0 && y >= 0) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						
						x=j+1;
						y=i+2;
						if((x < 9 && y < 10) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						x=j-1;
						y=i+2;
						if((x >= 0 && y < 10) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						x=j+1;
						y=i-2;
						if((x < 9 && y >= 0) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						x=j-1;
						y=i-2;
						if((x >= 0 && y >= 0) &&CanTouch(position, j, i, x, y))
							AddPoint(x, y);
						break;
						
//					case Chessconst.R_CAR:
//					case Chessconst.B_CAR:
//						x=j+1;
//						y=i;
//						while(x < 9)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//								AddPoint(x, y);
//							else
//							{
//								AddPoint(x, y);
//								break;
//							}
//							x++;
//						}
//
//						x = j-1;
//						y = i;
//						while(x >= 0)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//								AddPoint(x, y);
//							else
//							{
//								AddPoint(x, y);
//								break;
//							}
//							x--;
//						}
//
//						x=j;
//						y=i+1;//
//						while(y < 10)
//						{
//							if( Chessconst.NOCHESS == position[y][x])
//								AddPoint(x, y);
//							else
//							{
//								AddPoint(x, y);
//								break;
//							}
//							y++;
//						}
//
//						x = j;
//						y = i-1;
//						while(y>=0)
//						{
//							if( Chessconst.NOCHESS == position[y][x])
//								AddPoint(x, y);
//							else
//							{
//								AddPoint(x, y);
//								break;
//							}
//							y--;
//						}
//						break;
						
					case Chessconst.R_PAWN:
						y = i - 1;
						x = j;
						
						if(y >= 0)
							AddPoint(x, y);
						
						if(i < 5)
						{
							y=i;
							x=j+1;
							if(x < 9 )
								AddPoint(x, y);
							x=j-1;
							if(x >= 0 )
								AddPoint(x, y);
						}
						break;
						
					case Chessconst.B_PAWN:
						y = i + 1;
						x = j;
						
						if(y < 10 )
							AddPoint(x, y);
						
						if(i > 4)
						{
							y=i;
							x=j+1;
							if(x < 9)
								AddPoint(x, y);
							x=j-1;
							if(x >= 0)
								AddPoint(x, y);
						}
						break;
						
//					case Chessconst.B_CANON:
//					case Chessconst.R_CANON:
//
//						x=j+1;		//
//						y=i;
//						flag=false;
//						while(x < 9)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//							{
//								if(!flag)
//									AddPoint(x, y);
//							}
//							else
//							{
//								if(!flag)
//									flag=true;
//								else
//								{
//									AddPoint(x, y);
//									break;
//								}
//							}
//							x++;
//						}
//
//						x=j-1;
//						flag=false;
//						while(x>=0)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//							{
//								if(!flag)
//									AddPoint(x, y);
//							}
//							else
//							{
//								if(!flag)
//									flag=true;
//								else
//								{
//									AddPoint(x, y);
//									break;
//								}
//							}
//							x--;
//						}
//						x=j;
//						y=i+1;
//						flag=false;
//						while(y < 10)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//							{
//								if(!flag)
//									AddPoint(x, y);
//							}
//							else
//							{
//								if(!flag)
//									flag=true;
//								else
//								{
//									AddPoint(x, y);
//									break;
//								}
//							}
//							y++;
//						}
//
//						y=i-1;	//
//						flag=false;
//						while(y>=0)
//						{
//							if( Chessconst.NOCHESS == position[y][x] )
//							{
//								if(!flag)
//									AddPoint(x, y);
//							}
//							else
//							{
//								if(!flag)
//									flag=true;
//								else
//								{
//									AddPoint(x, y);
//									break;
//								}
//							}
//							y--;
//						}
//						break;
						
					default:
						break;
						
					}
					return nPosCount ;	
	}
	
	/**
	 * 判断棋盘position上位置From的棋子能否走到位置To
	 * @param position
	 * @param nFromX
	 * @param nFromY
	 * @param nToX
	 * @param nToY
	 * @return
	 */
	boolean CanTouch(int position[][], int nFromX, int nFromY, int nToX, int nToY){
		int i = 0, j = 0;
		int nMoveChessID, nTargetID;
		if (nFromY ==  nToY && nFromX == nToX)
			return false;//目的与源相同

		nMoveChessID = position[nFromY][nFromX];
		nTargetID = position[nToY][nToX];
		if (nTargetID == Chessconst.B_CAR)
			return false;//大山
		
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
//			if( nFromY < 5 && nFromY == nToY)
//				return false;//兵过河前只能直走
                if(nToY - nFromY + Math.abs(nToX - nFromX) > 1)
                    return false;//兵只走一步直线:
                break;

            case Chessconst.R_PAWN:    //红兵
                if(nToY > nFromY)
                    return false;//兵不回头
//			if( nFromY > 4 && nFromY == nToY)
//				return false;//兵过河前只能直走
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
	 * 将一个位置加入到数组RelatePos中
	 * @param x
	 * @param y
	 */
	void AddPoint(int x, int y){
		if(RelatePos[nPosCount] == null)
			RelatePos[nPosCount] = new Chessmanpos();
		RelatePos[nPosCount].x = x;
		RelatePos[nPosCount].y = y;
		nPosCount++;
	}
	
}
