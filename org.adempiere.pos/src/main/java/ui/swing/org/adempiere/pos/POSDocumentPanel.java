/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.pos;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.adempiere.plaf.AdempierePLAF;
import org.adempiere.pos.search.QueryBPartner;
import org.adempiere.pos.service.I_POSPanel;
import org.adempiere.pos.service.I_POSQuery;
import org.adempiere.pos.service.POSQueryListener;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerInfo;
import org.compiere.model.MPOSKey;
import org.compiere.pos.PosKeyListener;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *	Function Key Sub Panel
 *	
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright (c) Jorg Janke
 *  @author Mario Calderon, mario.calderon@westfalia-it.com, Systemhaus Westfalia, http://www.westfalia-it.com
 *  @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *  <li> Implement best practices
 *  @version $Id: SubFunctionKeys.java,v 1.1 2004/07/12 04:10:04 jjanke Exp $
 */
public class POSDocumentPanel extends POSSubPanel
	implements PosKeyListener, ActionListener, I_POSPanel, POSQueryListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2131406504920855582L;

	/**
	 * 	Constructor
	 *	@param posPanel POS Panel
	 */
	public POSDocumentPanel(VPOS posPanel) {
		super (posPanel);
	}	//	POSDocumentPanel
	
	/**	Header Panel		*/
	private CPanel 			headerPanel;
	/**	Total Panel			*/;
	private CPanel 			totalPanel;
	/**	Doc Info Panel		*/
	private CPanel 			documentInfoPanel;
	/**	Total Title Border	*/
	private TitledBorder 	totalTitle;
	/**	Doc Title Border	*/
	private TitledBorder 	documentTitle;
	/**	Sales Representative*/
	private CLabel 			labelSalesRep;
	private CLabel 			fieldSalesRep;
	/**	Document Type		*/
	private CLabel 			labelDocumentType;
	private CLabel 			fieldDocumentType;
	/**	Document No			*/
	private CLabel 			labelDocumentNo;
	private CLabel 			fieldDocumentNo;
	/**	Total Lines			*/
	private CLabel 			labelTotalLines;
	private CLabel 			fieldTotalLines;
	/**	Tax Amount			*/
	private CLabel 			labelTaxAmount;
	private CLabel 			fieldTaxAmount;
	/**	Grand Total			*/
	private CLabel 			labelGrandTotal;
	private CLabel 			fieldGrandTotal;
	/**	Product Name		*/
	private POSTextField 	fieldPartnerName;
	/**	Keyboard			*/
	private POSKeyPanel 	keyboardPanel;
	/**	Logger				*/
	private static CLogger logger = CLogger.getCLogger(POSDocumentPanel.class);

	/**
	 * 	Initialize
	 */
	public void init() {
		int posKeyLayoutId = posPanel.getC_POSKeyLayout_ID();
		//	Set Layout
		setLayout(new GridBagLayout());
		//	Set Right Padding
		int rightPadding = 0;
		//	Document Panel
		//	Set Font and Format
		headerPanel = new CPanel(new GridBagLayout());
		documentInfoPanel = new CPanel(new GridBagLayout());
		totalPanel = new CPanel(new GridBagLayout());
		//	For Doc Title Border
		documentTitle = BorderFactory.createTitledBorder(Msg.getMsg(Env.getCtx(), "InfoOrder"));
		documentTitle.setTitleFont(posPanel.getFont());
		documentTitle.setTitleColor(AdempierePLAF.getTextColor_Label());
		documentInfoPanel.setBorder(documentTitle);
		//	For Total Title Border
		totalTitle = BorderFactory.createTitledBorder(Msg.getMsg(Env.getCtx(), "Totals"));
		totalTitle.setTitleFont(posPanel.getFont());
		totalTitle.setTitleColor(AdempierePLAF.getTextColor_Label());
		totalPanel.setBorder(totalTitle);
		//	For Document Info
		//	For Sales Representative
		labelSalesRep = new CLabel (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_SalesRep_ID) + ":");
		labelSalesRep.setFont(posPanel.getPlainFont());
		//	Add
		documentInfoPanel.add(labelSalesRep, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldSalesRep = new CLabel();
		fieldSalesRep.setFont(posPanel.getFont());
		labelSalesRep.setLabelFor(fieldSalesRep);
		//	Add
		documentInfoPanel.add(fieldSalesRep, new GridBagConstraints(3, 0, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		//	For Tax Amount
		labelDocumentType = new CLabel (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_C_DocType_ID) + ":");
		labelDocumentType.setFont(posPanel.getPlainFont());
		//	Add
		documentInfoPanel.add(labelDocumentType, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldDocumentType = new CLabel();
		fieldDocumentType.setFont(posPanel.getFont());
		labelDocumentType.setLabelFor(fieldDocumentType);
		//	Add
		documentInfoPanel.add(fieldDocumentType, new GridBagConstraints(3, 1, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		//	For Grand Total
		labelDocumentNo = new CLabel (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_DocumentNo) + ":");
		labelDocumentNo.setFont(posPanel.getPlainFont());
		//	Add
		documentInfoPanel.add(labelDocumentNo, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldDocumentNo = new CLabel();
		fieldDocumentNo.setFont(posPanel.getFont());
		labelDocumentNo.setLabelFor(fieldDocumentNo);
		//	Change Size
		//	Add
		documentInfoPanel.add(fieldDocumentNo,  new GridBagConstraints(3, 3, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		
		//	For Totals
		//	For Total Lines
		labelTotalLines = new CLabel (Msg.getMsg(Env.getCtx(), "SubTotal") + ":");
		labelTotalLines.setFont(posPanel.getPlainFont());
		//	Add
		totalPanel.add(labelTotalLines, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldTotalLines = new CLabel();
		fieldTotalLines.setFont(posPanel.getFont());
		labelTotalLines.setLabelFor(fieldTotalLines);
		//	Add
		totalPanel.add(fieldTotalLines, new GridBagConstraints(3, 0, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		//	For Tax Amount
		labelTaxAmount = new CLabel (Msg.translate(Env.getCtx(), I_C_OrderLine.COLUMNNAME_C_Tax_ID) + ":");
		labelTaxAmount.setFont(posPanel.getPlainFont());
		//	Add
		totalPanel.add(labelTaxAmount, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldTaxAmount = new CLabel();
		fieldTaxAmount.setFont(posPanel.getFont());
		labelTaxAmount.setLabelFor(fieldTaxAmount);
		//	Add
		totalPanel.add(fieldTaxAmount, new GridBagConstraints(3, 1, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		//	For Grand Total
		labelGrandTotal = new CLabel (Msg.getMsg(posPanel.getCtx(), "Total") + ":");
		labelGrandTotal.setFont(posPanel.getFont());
		//	Add
		totalPanel.add(labelGrandTotal, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		//	
		fieldGrandTotal = new CLabel();
		fieldGrandTotal.setFont(posPanel.getBigFont());
		labelGrandTotal.setLabelFor(fieldGrandTotal);
		//	Change Size
		//	Add
		totalPanel.add(fieldGrandTotal,  new GridBagConstraints(3, 3, 1, 1, 1, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, rightPadding), 0, 0));
		
		//	Add Doc Info
		headerPanel.add(documentInfoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1
				,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
		//	Add to Header
		headerPanel.add(totalPanel, new GridBagConstraints(1, 0, 1, 1, 1, 1
				,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
		
		//	For Product
		String labelName = Msg.translate(Env.getCtx(), I_C_BPartner.COLUMNNAME_IsCustomer);
		//	
		fieldPartnerName = new POSTextField(labelName, posPanel.getKeyboard());
		fieldPartnerName.setName(labelName);
		fieldPartnerName.setPlaceholder(labelName);
		fieldPartnerName.addActionListener(this);
		fieldPartnerName.setFocusable(true);
		fieldPartnerName.setFont(posPanel.getFont());
		fieldPartnerName.setPreferredSize(new Dimension(530, 50));
		fieldPartnerName.setMinimumSize(new Dimension(530, 50));
		//	Add Header
		GridBagConstraints headerConstraint = new GridBagConstraints();
		headerConstraint.fill = GridBagConstraints.BOTH;
		headerConstraint.weightx = 1;
		headerConstraint.gridy = 0;
		add(headerPanel, headerConstraint);
		//	Add Product Name
		GridBagConstraints productConstraint = new GridBagConstraints();
		productConstraint.fill = GridBagConstraints.HORIZONTAL;
		productConstraint.weightx = 0;
		productConstraint.gridy = 1;
		productConstraint.gridwidth = 2;
		headerPanel.add(fieldPartnerName, productConstraint);
		//	Add Keyboard
		GridBagConstraints keyboardConstraint = new GridBagConstraints();
		keyboardConstraint.fill = GridBagConstraints.BOTH;
		keyboardConstraint.weightx = 1;
		keyboardConstraint.weighty = 1;
		keyboardConstraint.gridy = 2;
		//	For Key Panel
		keyboardPanel = new POSKeyPanel(posKeyLayoutId, this);
		add(keyboardPanel, keyboardConstraint);
		//	Refresh
		refreshPanel();
	}	//	init

	/**
	 * Call back from key panel
	 */
	public void keyReturned(MPOSKey key) {
		// processed order
		if (posPanel.hasOrder()
				&& posPanel.isCompleted()) {
			//	Show Product Info
			posPanel.refreshProductInfo(key);
			return;
		}
		// Add line
		posPanel.addLine(key.getM_Product_ID(), key.getQty());
		//	Show Product Info
		posPanel.refreshProductInfo(key);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		//	Name
		if (e.getSource() == fieldPartnerName) {
			if(posPanel.isDrafted() || posPanel.isInProgress())  {
				findBPartner();		
			}
		}
	}
	
	/**
	 * 	Find/Set BPartner
	 */
	private void findBPartner() {
		String query = fieldPartnerName.getText();
		//	
		if (query == null || query.length() == 0)
			return;
		
		// unchanged
		if (posPanel.hasBPartner()
				&& posPanel.compareBPName(query))
			return;
		
		query = query.toUpperCase();
		//	Test Number
		boolean allNumber = true;
		boolean noNumber = true;
		char[] qq = query.toCharArray();
		for (int i = 0; i < qq.length; i++) {
			if (Character.isDigit(qq[i])) {
				noNumber = false;
				break;
			}
		} try {
			Integer.parseInt(query);
		} catch (Exception e) {
			allNumber = false;
		}
		String value = query;
		String name = (allNumber ? null : query);
		String eMail = (query.indexOf('@') != -1 ? query : null);
		String phone = (noNumber ? null : query);
		String city = null;
		//
		MBPartnerInfo[] results = MBPartnerInfo.find(ctx, value, name,
			/*Contact, */null, eMail, phone, city);
		
		//	Set Result
		if (results.length == 1) {
			MBPartner bp = MBPartner.get(ctx, results[0].getC_BPartner_ID());
			posPanel.setC_BPartner_ID(bp.getC_BPartner_ID());
			fieldPartnerName.setText(bp.getName());
		} else {	//	more than one
			QueryBPartner qt = new QueryBPartner(posPanel);
			qt.addOptionListener(this);
			qt.setResults(results);
			qt.showView();
		}
		//	Default return
		posPanel.refreshPanel();
		return;
	}	//	findBPartner

	@Override
	public void refreshPanel() {
		logger.fine("RefreshPanel");
		if (!posPanel.hasOrder()) {
			//	Document Info
			fieldSalesRep.setText(posPanel.getSalesRepName());
			fieldDocumentType.setText(Msg.getMsg(posPanel.getCtx(), "Order"));
			fieldDocumentNo.setText(Msg.getMsg(posPanel.getCtx(), "New"));
			fieldTotalLines.setText(posPanel.getNumberFormat().format(Env.ZERO));
			fieldGrandTotal.setText(posPanel.getNumberFormat().format(Env.ZERO));
			fieldTaxAmount.setText(posPanel.getNumberFormat().format(Env.ZERO));
			fieldPartnerName.setText(null);
		} else {
			String currencyISOCode = posPanel.getCurSymbol();
			BigDecimal totalLines = posPanel.getTotalLines();
			BigDecimal grandTotal = posPanel.getGrandTotal();
			BigDecimal taxAmt = grandTotal.subtract(totalLines);
			//	Set Values
			//	Document Info
			fieldSalesRep.setText(posPanel.getSalesRepName());
			fieldDocumentType.setText(posPanel.getDocumentTypeName());
			fieldDocumentNo.setText(posPanel.getDocumentNo());
			fieldTotalLines.setText(currencyISOCode + " " + posPanel.getNumberFormat().format(totalLines));
			fieldGrandTotal.setText(currencyISOCode + " " + posPanel.getNumberFormat().format(grandTotal));
			fieldTaxAmount.setText(currencyISOCode + " " + posPanel.getNumberFormat().format(taxAmt));
			fieldPartnerName.setText(posPanel.getBPName());
		}
		//	Repaint
		totalPanel.validate();
		totalPanel.repaint();
	}

	@Override
	public String validatePanel() {
		return null;
	}

	@Override
	public void changeViewPanel() {
		if(posPanel.hasOrder()) {
			//	When order is not completed, you can change BP
			fieldPartnerName.setEnabled(!posPanel.isCompleted());
		} else {
			fieldPartnerName.setEnabled(true);
		}
	}

	@Override
	public void okAction(I_POSQuery query) {
		if (query.getRecord_ID() > 0) {
			fieldPartnerName.setText(query.getValue());
			if(!posPanel.hasOrder()) {
				posPanel.newOrder(query.getRecord_ID());
			} else {
				posPanel.setC_BPartner_ID(query.getRecord_ID());
			}
			//	
			logger.fine("C_BPartner_ID=" + query.getRecord_ID());
		}
	}

	@Override
	public void cancelAction(I_POSQuery query) {
		//	Nothing
	}

	@Override
	public void moveUp() {
	}

	@Override
	public void moveDown() {
	}
}	//	POSDocumentPanel