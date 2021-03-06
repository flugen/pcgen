/*
 * JTableEx.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 27, 2001, 20:36 PM
 */
package pcgen.gui2.util;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.SortableTableRowSorter;
import pcgen.gui2.util.table.TableCellUtilities;

/**
 *  {@code JTableEx} extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson &lt;jujutsunerd@users.sourceforge.net&gt;
 */
public class JTableEx extends JTable
{

	private static final long serialVersionUID = 514835142307946415L;

	/** Constant for a double click action event. */
	public static final int ACTION_DOUBLECLICK = 2042;
	private boolean sortingEnabled;

	/**
	 * Constructor
	 */
	public JTableEx()
	{
		this(null, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 */
	public JTableEx(TableModel tm)
	{
		this(tm, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 * @param tcm
	 */
	public JTableEx(TableModel tm, TableColumnModel tcm)
	{
		this(tm, tcm, null);
	}

	public JTableEx(TableModel tm, TableColumnModel tcm,
			ListSelectionModel lsm)
	{
		super(tm, tcm, lsm);
		setFillsViewportHeight(true);
		setDefaultRenderer(BigDecimal.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Float.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.RIGHT));
		installDoubleClickListener();
	}

	private void installDoubleClickListener()
	{
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					Point p = e.getPoint();
					int row = convertRowIndexToModel(rowAtPoint(p));
					int column = convertColumnIndexToModel(columnAtPoint(p));
					Object value = getModel().getValueAt(row, column);
					fireActionEvent(JTableEx.this, ACTION_DOUBLECLICK, String.valueOf(value));
				}
			}
		});
	}

	private void fireActionEvent(Object value, int id, String command)
	{
		ActionEvent e = null;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ActionListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new ActionEvent(value, id, command);
				}

				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	public void addActionListener(ActionListener listener)
	{
		listenerList.add(ActionListener.class, listener);
	}

    public void removeActionListener(ActionListener listener)
    {
        listenerList.remove(ActionListener.class, listener);
    }
	
	@Override
	public boolean getAutoCreateRowSorter()
	{
		return sortingEnabled;
	}

	@Override
	public void setAutoCreateRowSorter(boolean autoCreateRowSorter)
	{
		boolean oldValue = this.sortingEnabled;
		this.sortingEnabled = autoCreateRowSorter;
		if (sortingEnabled)
		{
			TableModel model = getModel();
			if (model instanceof SortableTableModel)
			{
				setRowSorter(new SortableTableRowSorter((SortableTableModel) dataModel));
			}
			else
			{
				setRowSorter(new TableRowSorter(model));
			}
		}
		firePropertyChange("autoCreateRowSorter", oldValue,
				autoCreateRowSorter);
	}

	public void sortModel()
	{
		RowSorter rowSorter = getRowSorter();
		if (rowSorter != null)
		{
			rowSorter.setSortKeys(getRowSorter().getSortKeys());
		}
	}

	@Override
	public void setModel(TableModel dataModel)
	{
		if (dataModel == null)
		{
			throw new IllegalArgumentException("Cannot set a null TableModel");
		}
		if (this.dataModel != dataModel)
		{
			TableModel old = this.dataModel;
			if (old != null)
			{
				old.removeTableModelListener(this);
			}
			this.dataModel = dataModel;
			dataModel.addTableModelListener(this);

			tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));

			firePropertyChange("model", old, dataModel);

			if (getAutoCreateRowSorter())
			{
				if (dataModel instanceof SortableTableModel)
				{
					super.setRowSorter(new SortableTableRowSorter((SortableTableModel) dataModel));
				}
				else
				{
					super.setRowSorter(new TableRowSorter(dataModel));
				}
			}
		}
	}

	/**
	 * set horizontal alignment of column
	 * and attach a new cell renderer
	 * @param col
	 * @param alignment
	 **/
	public void setColAlign(int col, int alignment)
	{
		getColumnModel().getColumn(col).setCellRenderer(
				new TableCellUtilities.AlignRenderer(alignment));
	}

}
