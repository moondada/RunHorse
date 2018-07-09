package simple.game.chess;


import android.net.sip.SipSession;

import java.io.Serializable;

import com.util.JsonBeanUtil;
import com.util.MapAnnotation.Map;

public class Chess_Status implements Serializable {

	@Map(key = "oldx")
	private int oldx;
	@Map(key = "oldy")
	private int oldy;
	@Map(key = "cx")
	private int cx;
	@Map(key = "cy")
	private int cy;
	@Map(key = "ischange")
	boolean ischange=false;

	@Map(key = "isfirst")
	boolean isfirst=false;


	@Map(key = "conn")
	boolean conn=false;
	@Map(key = "message")
	private String message;
	@Map(key = "items")
	private String items = null;
    @Map(key = "items2")
    private String items2 = null;
	@Map(key = "State")
	private String State = null;
	@Map(key = "State")
	private String State2 = null;
	@Map(key = "nx")
	private int nx = -1;
	@Map(key = "ny")
	private int ny = -1;
	@Map(key = "itype")
	private int itype = -1;
	
	public boolean isConn() {
		return conn;
	}
	public void setConn(boolean conn) {
		this.conn = conn;
	}
	public boolean isIsfirst() {
		return isfirst;
	}
	public void setIsfirst(boolean isfirst) {
		this.isfirst = isfirst;
	}
	public int getOldx() {
		return oldx;
	}
	public void setOldx(int oldx) {
		this.oldx = oldx;
	}
	public int getOldy() {
		return oldy;
	}
	public void setOldy(int oldy) {
		this.oldy = oldy;
	}
	public int getCx() {
		return cx;
	}
	public void setCx(int cx) {
		this.cx = cx;
	}
	public int getCy() {
		return cy;
	}
	public void setCy(int cy) {
		this.cy = cy;
	}

	public int getNx() {
		return nx;
	}
	public int getNy() {
		return ny;
	}
	public int getItype() {
		return itype;
	}
	public boolean isIschange() {
		return ischange;
	}
	public void setIschange(boolean ischange) {
		this.ischange = ischange;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    public void setItem( Chess_Item_Base newItem) {
		if(newItem!=null){
			nx = newItem.getCentx();
			ny = newItem.getCenty();
			itype = newItem.getType();
		}
    }
	public String getItems() {
		return items;
	}
    public String getItems2() {
        return items2;
    }
	public String getState() {
		return State;
	}
	public String getState2() {
		return State2;
	}
	public void setItems() {
		StringBuffer Items = new StringBuffer();
		StringBuffer Items2 = new StringBuffer();
		StringBuffer state = new StringBuffer();
		StringBuffer state2 = new StringBuffer();
		for(int i=0;i<90;i++){
			Items.append('0');
			state.append('t');
			Items2.append('0');
			state2.append('f');
		}
		Items.setCharAt(4+1*9,'2');
		Items.setCharAt(4+0*9,'5');
		Items.setCharAt(4+8*9,'2');
		Items.setCharAt(4+9*9,'5');
		Items2.setCharAt(89-(4+1*9),'2');
		Items2.setCharAt(89-(4+0*9),'5');
		Items2.setCharAt(89-(4+8*9),'2');
		Items2.setCharAt(89-(4+9*9),'5');
		state.setCharAt(4+1*9,'t');
		state.setCharAt(4+0*9,'t');
		state.setCharAt(4+8*9,'f');
		state.setCharAt(4+9*9,'f');
		state2.setCharAt(89-(4+1*9),'f');
		state2.setCharAt(89-(4+0*9),'f');
		state2.setCharAt(89-(4+8*9),'t');
		state2.setCharAt(89-(4+9*9),'t');
		for(int i = 0;i<4;i++){
			int x=(int)(Math.random()*8);
			int y=(int)(Math.random()*3+2);
			while(Items.charAt(x+9*y) != '0' || Items.charAt(x+9*(y+1)) != '0'
					|| Items.charAt(x+1+9*(y)) != '0' || Items.charAt(x+1+9*(y+1)) != '0' ){
				x=(int)(Math.random()*8);
				y=(int)(Math.random()*3+2);
			}
			Items.setCharAt(x+9*y,'1');
			state.setCharAt(x+9*y,'t');
			Items2.setCharAt(89-(x+9*y),'1');
			state2.setCharAt(89-(x+9*y),'f');
		}
		for(int i = 0;i<4;i++){
            int x=(int)(Math.random()*8);
            int y=(int)(Math.random()*3+5);
            while(Items.charAt(x+9*y) != '0' || Items.charAt(x+9*(y-1)) != '0'
                    || Items.charAt(x+1+9*(y)) != '0' || Items.charAt(x+1+9*(y-1)) != '0' ){
                x=(int)(Math.random()*8);
                y=(int)(Math.random()*3+5);
            }
            Items.setCharAt(x+9*y,'1');
            state.setCharAt(x+9*y,'t');
            Items2.setCharAt(89-(x+9*y),'1');
            state2.setCharAt(89-(x+9*y),'f');
        }
        int x=(int)(Math.random()*8);
        int y=(int)(Math.random()*6+2);
        while(Items.charAt(x+9*y) != '0' || Items.charAt(x+9*(y-1)) != '0'
                || Items.charAt(x+1+9*(y)) != '0' || Items.charAt(x+1+9*(y-1)) != '0' ){
            x=(int)(Math.random()*8);
            y=(int)(Math.random()*6+2);
        }
        Items.setCharAt(x+9*y,'6');
        state.setCharAt(x+9*y,'f');
        Items2.setCharAt(89-(x+9*y),'6');
        state2.setCharAt(89-(x+9*y),'t');
		items = Items.toString();
        items2 = Items2.toString();
		State = state.toString();
		State2 = state2.toString();
	}
}
