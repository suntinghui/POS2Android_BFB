/**
 * 
 */
package com.bfb.pos.dynamic.template.os;

import java.util.Vector;

import com.bfb.pos.dynamic.component.ViewException;
import com.bfb.pos.dynamic.core.ViewPage;

import android.view.View;

/**
 * @author DongXiaoping
 *
 */
public interface IStructTemplate {
	public Vector<View> rewind(ViewPage viewPage) throws ViewException;
}
