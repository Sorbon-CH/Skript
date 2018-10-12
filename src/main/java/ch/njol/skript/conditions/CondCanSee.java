/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.eclipse.jdt.annotation.Nullable;

@Name("Can See")
@Description("Checks whether the given players can see another players.")
@Examples({"if the player can't see the player-argument:",
		"	message \"<light red>The player %player-argument% is not online!\""})
@Since("2.3")
public class CondCanSee extends Condition {

	static {
		Skript.registerCondition(CondCanSee.class,
				"%players% (is|are) [(1¦in)]visible for %players%",
				"%players% can see %players%",
				"%players% (is|are)(n't| not) [(1¦in)]visible for %players%",
				"%players% can('t| not) see %players%");
	}
	
	@SuppressWarnings("null")
	private Expression<Player> players, targetPlayers;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setNegated(matchedPattern > 1 ^ parseResult.mark == 1);
		if (matchedPattern == 1 || matchedPattern == 3) {
			players = (Expression<Player>) exprs[0];
			targetPlayers = (Expression<Player>) exprs[1];
		} else {
			players = (Expression<Player>) exprs[1];
			targetPlayers = (Expression<Player>) exprs[0];
		}
		return true;
	}

	@Override
	public boolean check(Event e) {
		return players.check(e, new Checker<Player>() {
			@Override
			public boolean check(final Player player) {
				return targetPlayers.check(e, new Checker<Player>() {
					@Override
					public boolean check(final Player targetPlayer) {
						return player.canSee(targetPlayer);
					}
				}, isNegated());
			}
		});
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return players.toString(e, debug) + (isNegated() ? " can't see " : " can see ") + targetPlayers.toString(e, debug);
	}

}