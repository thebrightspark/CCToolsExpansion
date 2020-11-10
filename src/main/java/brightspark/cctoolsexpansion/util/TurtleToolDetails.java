package brightspark.cctoolsexpansion.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TurtleToolDetails {
	public String itemId;
	public String adjective;
	public TurtleToolType type;

	public TurtleToolDetails(String itemId, String adjective, TurtleToolType type) {
		this.itemId = itemId;
		this.adjective = adjective;
		this.type = type;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
			.append("itemId", itemId)
			.append("adjective", adjective)
			.append("type", type)
			.toString();
	}
}
