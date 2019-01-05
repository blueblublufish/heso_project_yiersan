package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.coupon.CouponService;
import com.heso.service.coupon.entity.GetCouponObject;
import com.heso.service.coupon.entity.GetCouponServiceReturnObject;
import com.heso.service.coupon.entity.UserCouponObject;
import com.heso.service.coupon.entity.UserCouponServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 优惠券查询,领券列表页面
 * @author dengjiahao
 *
 */
public class Transaction_001042 extends AbstractInterfaceClass {

		private static final Log logger = LogFactory.getLog(Transaction_001042.class);
		
		@Override
		public String execute(Node xmlBody, String IPAddress) {
			try {
				//取出数据
				String account = xmlBody.selectSingleNode("account").getText();
				String recStart = xmlBody.selectSingleNode("recStart").getText();
				String recCount = xmlBody.selectSingleNode("recCount").getText();
				
				//设置返回数据
				StringBuffer sb = new StringBuffer();
				
				//优惠券查询,
				GetCouponServiceReturnObject gcsro = new CouponService().getCoupon(account, Integer.parseInt(recStart), Integer.parseInt(recCount));
				if (gcsro.getCode().equals("000000")) {
					sb.append("<recCount>" + gcsro.getRecCount() + "</recCount>");
					if(gcsro.getCoList()!=null){
						for(GetCouponObject gco : gcsro.getCoList()){
							sb.append("<couponItem>");
							sb.append("<coupongen_id>"+gco.getCoupongen_id()+"</coupongen_id>");
							sb.append("<coupon_state>"+gco.getCoupon_state()+"</coupon_state>");
							sb.append("<coupon_name>"+gco.getCoupon_name()+"</coupon_name>");
							sb.append("<coupon_price>"+gco.getCoupon_price()+"</coupon_price>");
							sb.append("<min_consumption>"+gco.getMin_consumption()+"</min_consumption>");
							sb.append("<discount_rate>"+gco.getDiscount_rate()+"</discount_rate>");
							sb.append("<start_time>"+gco.getStart_time()+"</start_time>");
							sb.append("<end_time>"+gco.getEnd_time()+"</end_time>");
							sb.append("<coupon_type>"+gco.getCoupon_type()+"</coupon_type>");
							sb.append("<validtime>"+gco.getValidTime()+"</validtime>");
							sb.append("</couponItem>");
						}
					}
				}
				
				String xmlBodyStr = super.buildResp(gcsro.getCode(), sb.toString());
				return xmlBodyStr;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.ERROR_RETURN;
		}
		

}
