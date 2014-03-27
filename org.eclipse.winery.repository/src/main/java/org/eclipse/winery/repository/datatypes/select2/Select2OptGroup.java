package org.eclipse.winery.repository.datatypes.select2;

import java.util.SortedSet;
import java.util.TreeSet;

public class Select2OptGroup implements Comparable<Select2OptGroup> {
	
	private final String text;
	private final SortedSet<Select2DataItem> children;
	
	
	public Select2OptGroup(String text) {
		this.text = text;
		this.children = new TreeSet<Select2DataItem>();
	}
	
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns the internal SortedSet for data items.
	 */
	public SortedSet<Select2DataItem> getChildren() {
		return this.children;
	}
	
	public void addItem(Select2DataItem item) {
		this.children.add(item);
	}
	
	/**
	 * Quick hack to test Select2OptGroups for equality. Only the text is
	 * tested, not the contained children. This might cause issues later, but
	 * currently not.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Select2OptGroup)) {
			return false;
		}
		return this.text.equals(((Select2OptGroup) o).text);
	}
	
	/**
	 * Quick hack to compare Select2OptGroups. Only the text is compared, not
	 * the contained children. This might cause issues later, but currently not.
	 */
	@Override
	public int compareTo(Select2OptGroup o) {
		return this.getText().compareTo(o.getText());
	}
	
}
