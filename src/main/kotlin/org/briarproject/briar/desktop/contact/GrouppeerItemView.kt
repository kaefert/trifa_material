/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun GrouppeerItemView(
    grouppeerItem: GroupPeerItem,
    modifier: Modifier = Modifier,
) = Row(
    horizontalArrangement = spacedBy(8.dp),
    verticalAlignment = CenterVertically,
    modifier = modifier
        // allows content to be bottom-aligned
        .height(IntrinsicSize.Min)
) {
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(0.dp),
        modifier = Modifier.weight(1f, fill = true),
    ) {
        Box(Modifier.align(Top).padding(vertical = 8.dp)) {
            GroupPeerItemRoleCircle(Modifier.size(15.dp), grouppeerItem)
            //NumberBadge(
            //    num = contactItem.unread,
            //    modifier = Modifier.align(TopEnd).offset(6.dp, (-6).dp)
            //)
        }
        Spacer(Modifier.width(2.dp))
        GrouppeerItemViewInfo(
            grouppeerItem = grouppeerItem,
        )
    }
    ConnectionIndicator(
        modifier = Modifier.padding(end = 5.dp).requiredSize(16.dp),
        isConnected = grouppeerItem.connectionStatus
    )
}

@Composable
fun GroupPeerItemRoleCircle(
    modifier: Modifier = Modifier.size(10.dp),
    grouppeerItem: GroupPeerItem,
) = Box(
    modifier = modifier
        .border(1.dp, Color.Black, CircleShape)
        .background(GroupPeerRoleAsBgColor(grouppeerItem.peerRole), CircleShape)
)
{
    Text(text = "" + GroupPeerRoleAsStringShort(grouppeerItem.peerRole),
        modifier = Modifier.align(Alignment.Center),
        style = TextStyle(fontSize = 10.sp)
    )
}

fun GroupPeerRoleAsStringShort(peerRole: Int) : String
{
    if (peerRole == 0) // Founder
    {
        return "*"
    }
    else if (peerRole == 1) // Moderator
    {
        return "@"
    }
    else if (peerRole == 2) // User
    {
        return ""
    }
    else // Observer (muted)
    {
        return "~"
    }
}

fun GroupPeerRoleAsBgColor(peerRole: Int) : Color
{
    if (peerRole == 0) // Founder
    {
        return Color.Magenta
    }
    else if (peerRole == 1) // Moderator
    {
        return Color.Green
    }
    else if (peerRole == 2) // User
    {
        return Color.Transparent
    }
    else // Observer (muted)
    {
        return Color.Blue
    }
}

@Composable
private fun GrouppeerItemViewInfo(grouppeerItem: GroupPeerItem) = Column(
    horizontalAlignment = Start,
    modifier = Modifier.padding(start = 0.dp)
) {
    Text(
        text = if (grouppeerItem.name.isEmpty()) grouppeerItem.pubkey.toUpperCase().take(6) else grouppeerItem.name,
        style = if (grouppeerItem.name.length > 14) MaterialTheme.typography.body1.copy(fontSize = 12.sp) else MaterialTheme.typography.body1,
        maxLines = 1,
        overflow = Ellipsis,
    )
}
