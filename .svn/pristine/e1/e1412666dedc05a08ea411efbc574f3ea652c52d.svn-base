package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.coupon.CouponService;
import com.heso.service.coupon.entity.CouponServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 领取优惠券功能
 * @author dengjiahao
 *
 */
public class Transaction_001040 extends AbstractInterfaceClass {

		private static final Log logger = LogFactory.getLog(Transaction_001040.class);
		
		@Override
		public String execute(Node xmlBody, String IPAddress) {
			try {
				//取出数据
				String mobile = xmlBody.selectSingleNode("mobile").getText();
				String couponGenId = xmlBody.selectSingleNode("couponGenId").getText();//优惠券总表ID

				//领取优惠券
				CouponServiceReturnObject cro = new  CouponService().getCoupon(mobile,couponGenId);
				
				//设置返回数据
				StringBuffer sb = new StringBuffer();
				String xmlBodyStr = super.buildResp(cro.getCode(), sb.toString());
				return xmlBodyStr;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.ERROR_RETURN;
		}
		

}
