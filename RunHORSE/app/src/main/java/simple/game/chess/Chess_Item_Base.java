package simple.game.chess;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.util.MapAnnotation.Map;

public class Chess_Item_Base{

	@Map(key = "centx")
	private int centx;
	@Map(key = "centy")
	private int centy;
	@Map(key = "type")
	private int type = 0;
	@Map(key = "red")
	private boolean red;// 默认上红下黑
	@Map(key = "first")
	private boolean first;// 先手

	public Chess_Item_Base(){
		super();
	}
	
	public Chess_Item_Base(int x, int y, int type, boolean red) {
		this.centx = x;
		this.centy = y;
		this.type = type;
		this.red = red;
		this.first = red;
	}

	/**
	 * 先手，取反
	 */
	public void changeFirst()
	{
		first = !first;
	}
	
	public Chess_Item_Base(int x, int y) {
		this.centx = x;
		this.centy = y;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isRed() {
		return red;
	}
	// 是自己这一方   还是对方
	public boolean isSelf(Chess_Item_Base item) {
		if (item.getType() == 6)
			return false;
		if (item.isRed() == red )
			return true;
		return false;
	}

	public void setRed(boolean red) {
		this.red = red;
	}

	public int getCentx() {
		return centx;
	}

	public void setCentx(int centx) {
		this.centx = centx;
	}

	public int getCenty() {
		return centy;
	}

	public void setCenty(int centy) {
		this.centy = centy;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean inside(int x, int y) { 
		return centx == x && centy == y;
	}

	public String getText() {
		switch (this.type) {
			case 0:
				if (first)
					return "兵";
				return "卒";
			case 1:
				return "〇";
			case 2:
				return "马";
	
			case 3:
				if(first)
					return "象";
				return "相";
			case 4:
				return "士";
			case 5:
				if (first)
					return "帅";
				return "将";
			case 6:
				return "〇";
			default:
				break;
		}
		return "";
	}

	public void addSteps(List<Chess_Item_Base> items,List<Chess_Item_Base> steps) {

		// 马的走法
		if (getType() == 2) {
			List<Chess_Item_Base> tempxiang = new ArrayList<Chess_Item_Base>();
			Chess_Item_Base item1 = new Chess_Item_Base(getCentx() + 2,
					getCenty() + 1);
			Chess_Item_Base item2 = new Chess_Item_Base(getCentx() + 1,
					getCenty() + 2);
			Chess_Item_Base item3 = new Chess_Item_Base(getCentx() - 2,
					getCenty() + 1);
			Chess_Item_Base item4 = new Chess_Item_Base(getCentx() - 1,
					getCenty() + 2);
			Chess_Item_Base item5 = new Chess_Item_Base(getCentx() + 2,
					getCenty() - 1);
			Chess_Item_Base item6 = new Chess_Item_Base(getCentx() + 1,
					getCenty() - 2);
			Chess_Item_Base item7 = new Chess_Item_Base(getCentx() - 2,
					getCenty() - 1);
			Chess_Item_Base item8 = new Chess_Item_Base(getCentx() - 1,
					getCenty() - 2);
			tempxiang.add(item1);
			tempxiang.add(item2);
			tempxiang.add(item3);
			tempxiang.add(item4);
			tempxiang.add(item5);
			tempxiang.add(item6);
			tempxiang.add(item7);
			tempxiang.add(item8);
			a: for (int i = 0; i < tempxiang.size(); i++) {
				Chess_Item_Base temp = tempxiang.get(i);
				if (temp.getCentx() < 0 || temp.getCenty() < 0
						|| temp.getCenty() > 9 || temp.getCentx() > 8) {
					tempxiang.remove(temp);
					i--;
					continue a;
				}
				int df = temp.getCenty() - centy;
				int dx = temp.getCentx() - centx;
				Chess_Item_Base piejiao = new Chess_Item_Base(-1, -1);
				if (df == -2 || df == 2) {
					piejiao = new Chess_Item_Base(getCentx(), getCenty() + df
							/ 2);
				}
				if (dx == -2 || dx == 2) {
					piejiao = new Chess_Item_Base(getCentx() + dx / 2,
							getCenty());
				}
				b: for (Chess_Item_Base item : items) {
					if (item.inside(piejiao.getCentx(), piejiao.getCenty())) {

						continue a;
					} else if (item.inside(temp.getCentx(), temp.getCenty())) {
						if ((!item.isSelf(this) && item.getType()!=1) || item.getType()==6) {
							steps.add(temp);
						}
						continue a;
					}
				}
				steps.add(temp);
			}

		}

	}

}