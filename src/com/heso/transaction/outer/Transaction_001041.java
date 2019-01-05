package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.coupon.CouponService;
import com.heso.service.coupon.entity.UserCouponObject;
import com.heso.service.coupon.entity.UserCouponServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 用户优惠券查询
 * @author dengjiahao
 *
 */
public class Transaction_001041 extends AbstractInterfaceClass {

		private static final Log logger = LogFactory.getLog(Transaction_001041.class);
		
		@Override
		public String execute(Node xmlBody, String IPAddress) {
			try {
				//取出数据
				String account = xmlBody.selectSingleNode("account").getText();
				String coupon_state = xmlBody.selectSingleNode("coupon_state").getText();
				String recStart = xmlBody.selectSingleNode("recStart").getText();
				String recCount = xmlBody.selectSingleNode("recCount").getText();
				
				//设置返回数据
				StringBuffer sb = new StringBuffer();
				
				//用户优惠券查询
				UserCouponServiceReturnObject ucsro = new CouponService().getUserCoupon(account, coupon_state, Integer.parseInt(recStart), Integer.parseInt(recCount));
				if (ucsro.getCode().equals("000000")) {
					if(ucsro.getUco()!=null&&ucsro.getUco().size()!=0){
						
						sb.append("<recCount>" + ucsro.getRecCount() + "</recCount>");
						
						for(UserCouponObject uco : ucsro.getUco()){
							sb.append("<userCouponItem>");
							sb.append("<account>"+uco.getAccount()+"</account>");
							sb.append("<coupongen_id>"+uco.getCoupongen_id()+"</coupongen_id>");
							sb.append("<coupondet_id>"+uco.getCoupondet_id()+"</coupondet_id>");
							sb.append("<coupon_state>"+uco.getCoupon_state()+"</coupon_state>");
							sb.append("<get_time>"+uco.getGet_time()+"</get_time>");
							sb.append("<use_time>"+uco.getUse_time()+"</use_time>");
							sb.append("<order_id>"+uco.getOrder_id()+"</order_id>");
							sb.append("<coupon_name>"+uco.getCoupon_name()+"</coupon_name>");
							sb.append("<coupon_price>"+uco.getCoupon_price()+"</coupon_price>");
							sb.append("<start_time>"+uco.getStart_time()+"</start_time>");
							sb.append("<end_time>"+uco.getEnd_time()+"</end_time>");
							sb.append("<coupon_type>"+uco.getCoupon_type()+"</coupon_type>");
							sb.append("<validtime>"+uco.getValidTime()+"</validtime>");
							sb.append("<min_consumption>"+uco.getMin_consumption()+"</min_consumption>");
							sb.append("<discount_rate>"+uco.getDiscount_rate()+"</discount_rate>");
							sb.append("</userCouponItem>");
						}
					}else {
						sb.append("<recCount>0</recCount>");
					}
				}
				
				String xmlBodyStr = super.buildResp(ucsro.getCode(), sb.toString());
				return xmlBodyStr;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.ERROR_RETURN;
		}
		
		public static void main(String[] args) {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='utf-8'?>");
			sb.append("<message>");
			sb.append("<head>");
			sb.append("<type>001041</type>");
			sb.append("<messageId>1</messageId>");
			sb.append("<agentId>001</agentId>");
			sb.append("<digest>MD5数字签名</digest>");
			sb.append("</head>");
			sb.append("<body>");
			sb.append("<coupon_state>1</coupon_state>");
			sb.append("<account>0000000000000909</account>");
			sb.append("<token>0</token>");	
			sb.append("<recStart>0</recStart>");
			sb.append("<recCount>100</recCount>");
			sb.append("</body>");
			sb.append("</message>");
			try {
				new TransDataProcess().execute(sb.toString());
				System.out.println(">>>>>>>>>>>>>>gg");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
}
