package com.bfb.pos.dynamic.template.os.struct;

import java.util.Vector;

import android.view.View;

import com.bfb.pos.dynamic.core.ViewPage;
import com.bfb.pos.dynamic.template.StructTemplate;

public class FunctionPageTemplate extends StructTemplate {

	public FunctionPageTemplate(String id, String name) {
		super(id, name);
	}

	@Override
	public void dateInit() {
		ViewPage templatePage = this.getTemplatePage();
		if (null == templatePage) {
			return;
		}
		if (null != templatePage.getTarget()) {
			int i =0;
			/**
			 * 标注已经加载过模板了
			 */
			this.getCurrentPage().setTemplate(null);
			if (templatePage.getTarget().isPage()) {
				Vector<String> comIndex = new Vector<String>();
				for ( ;i<templatePage.getViewIndex().size(); i++) {
					if (null != this.getCurrentPage().getComponent(templatePage.getViewIndex().get(i))) {
						continue;
					}
					if (templatePage.getViewIndex().get(i).equals(templatePage.getTarget().getId())) {
						i++;
						break;
					}
					/**
					 * 定位到标签前的所有组件
					 */
					comIndex.add(templatePage.getViewIndex().get(i));
				}
				this.getCurrentPage().getViewIndex().addAll(0, comIndex);
				/**
				 * 增加标签后的所有的组件
				 */
				for (; i<templatePage.getViewIndex().size(); i++) {
					if (null != this.getCurrentPage().getComponent(templatePage.getViewIndex().get(i))) {
						continue;
					}
					this.getCurrentPage().getViewIndex().add(templatePage.getViewIndex().get(i));
				}
			} else {
				// TODO 针对某一类标签进行替换
			}
		} else {
			/**
			 * 没有替换标签，则全部加载到当前界面之后
			 */
			this.getCurrentPage().getViewIndex().addAll(templatePage.getViewIndex());
		}
	}

	@Override
	public Vector<View> excute() {
		// TODO Auto-generated method stub
		return null;
	}
}
