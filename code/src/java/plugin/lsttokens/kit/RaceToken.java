/*
 * RaceToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Race;
import pcgen.core.kit.KitRace;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Handles the RACE tag as well as Common tags on the RACE line.
 */
public class RaceToken extends AbstractToken implements
		CDOMSecondaryToken<KitRace>
{
	private static final Class<Race> RACE_CLASS = Race.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "RACE";
	}

	public Class<KitRace> getTokenClass()
	{
		return KitRace.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitRace kitRace, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (Constants.s_NONESELECTED.equals(value))
		{
			Logging
				.deprecationPrint("NONESELECTED is not necessary in KIT RACE: "
					+ "Token is not processed");
			return true;
		}
		CDOMSingleRef<Race> ref =
				context.ref.getCDOMReference(RACE_CLASS, value);
		kitRace.setRace(ref);
		return true;
	}

	public String[] unparse(LoadContext context, KitRace kitRace)
	{
		CDOMReference<Race> race = kitRace.getRace();
		if (race == null)
		{
			return null;
		}
		return new String[]{race.getLSTformat()};
	}

}
