/**
 * @Description:
 * @author: lvgch
 * @date 2022-05-21 07:20:22 
 */
package com.thtf.office.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvgch
 *
 * @date 2022-05-21 07:20:22
 */
public class ManuaPage {
	public static List getPageSizeDataForRelations(List datas, int pageSize, int pageNo) {

		int startNum = (pageNo - 1) * pageSize + 1; // 起始截取数据位置

		if (startNum > datas.size()) {

			return new ArrayList();

		}

		List res = new ArrayList<>();

		int rum = datas.size() - startNum;

		if (rum < 0) {

			return new ArrayList();

		}

		if (rum == 0) { // 说明正好是最后一个了

			List resLast = datas;

			int index = datas.size() - 1;

			return (List) resLast.get(index);

		}

		if (rum / pageSize >= 1) { // 剩下的数据还够1页，返回整页的数据

			for (int i = startNum; i < pageSize; i++)

				res.add(datas.get(i - 1));
			return res;
		}

	

		if((rum/pageSize==0)&&rum>0){//不够一页直接返回剩下数据
			for (int j = startNum; j <= datas.size(); j++) {
				res.add(datas.get(j - 1));
			}
			return res;

		}
		return new ArrayList();

	}
}
