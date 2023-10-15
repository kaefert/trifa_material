import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.zoffcc.applications.trifa.HelperGroup.tox_group_by_groupid__wrapper
import com.zoffcc.applications.trifa.MainActivity
import com.zoffcc.applications.trifa.MainActivity.Companion.tox_friend_by_public_key
import com.zoffcc.applications.trifa.MainActivity.Companion.tox_friend_send_message
import com.zoffcc.applications.trifa.MainActivity.Companion.tox_group_send_message
import com.zoffcc.applications.trifa.StateContacts
import com.zoffcc.applications.trifa.StateGroups
import com.zoffcc.applications.trifa.TRIFAGlobals
import com.zoffcc.applications.trifa.ToxVars
import com.zoffcc.applications.trifa.ToxVars.TOX_MESSAGE_TYPE
import com.zoffcc.applications.trifa.createContactStore
import com.zoffcc.applications.trifa.createGroupStore
import com.zoffcc.applications.trifa.createSavepathStore
import com.zoffcc.applications.trifa.createToxDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

private const val TAG = "trifa.Chatapp"
val myUser = User("Me", picture = null, toxpk = null)
val messagestore = CoroutineScope(SupervisorJob()).createMessageStore()
val groupmessagestore = CoroutineScope(SupervisorJob()).createGroupMessageStore()
val contactstore = CoroutineScope(SupervisorJob()).createContactStore()
val groupstore = CoroutineScope(SupervisorJob()).createGroupStore()
val savepathstore = CoroutineScope(SupervisorJob()).createSavepathStore()
val toxdatastore = CoroutineScope(SupervisorJob()).createToxDataStore()

@Composable
fun ChatAppWithScaffold(displayTextField: Boolean = true, contactList: StateContacts)
{
    Theme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    contactList.selectedContact?.let { Text(it.name) }
                },
                backgroundColor = MaterialTheme.colors.background,
            )
        }) {
            ChatApp(displayTextField = displayTextField, contactList.selectedContactPubkey)
        }
    }
}

@Composable
fun GroupAppWithScaffold(displayTextField: Boolean = true, groupList: StateGroups)
{
    Theme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    groupList.selectedGroup?.let { Text(it.name) }
                },
                backgroundColor = MaterialTheme.colors.background,
            )
        }) {
            GroupApp(displayTextField = displayTextField, groupList.selectedGroupId)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ChatApp(displayTextField: Boolean = true, selectedContactPubkey: String?)
{
    val state by messagestore.stateFlow.collectAsState()
    Theme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(painterResource("background.jpg"), modifier = Modifier.fillMaxSize(), contentDescription = null, contentScale = ContentScale.Crop)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        Messages(state.messages)
                    }
                    if (displayTextField)
                    {
                        SendMessage { text -> //
                            // Log.i(TAG, "selectedContactPubkey=" + selectedContactPubkey)
                            val friend_num = tox_friend_by_public_key(selectedContactPubkey)
                            val timestamp = System.currentTimeMillis()
                            val res = tox_friend_send_message(friend_num, TOX_MESSAGE_TYPE.TOX_MESSAGE_TYPE_NORMAL.value, text)
                            if (res >= 0)
                            {
                                MainActivity.sent_message_to_db(selectedContactPubkey, timestamp, text)
                                messagestore.send(MessageAction.SendMessage(UIMessage(myUser, timeMs = timestamp, text, toxpk = myUser.toxpk)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GroupApp(displayTextField: Boolean = true, selectedGroupId: String?)
{
    val state by groupmessagestore.stateFlow.collectAsState()
    Theme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(painterResource("background.jpg"), modifier = Modifier.fillMaxSize(), contentDescription = null, contentScale = ContentScale.Crop)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        GroupMessages(state.groupmessages)
                    }
                    if (displayTextField)
                    {
                        GroupSendMessage { text ->
                            val timestamp = System.currentTimeMillis()
                            val groupnum: Long = tox_group_by_groupid__wrapper(selectedGroupId!!)
                            val message_id: Long = tox_group_send_message(groupnum, ToxVars.TOX_MESSAGE_TYPE.TOX_MESSAGE_TYPE_NORMAL.value, text)
                            if (message_id >= 0)
                            {
                                MainActivity.sent_groupmessage_to_db(groupid = selectedGroupId, message_timestamp =  timestamp, group_message = text, message_id = message_id )
                                groupmessagestore.send(GroupMessageAction.SendGroupMessage(UIGroupMessage(myUser, timeMs = timestamp, text, toxpk = myUser.toxpk, groupId = selectedGroupId!!.lowercase(),
                                    trifaMsgType = TRIFAGlobals.TRIFA_MSG_TYPE.TRIFA_MSG_TYPE_TEXT.value, filename_fullpath = null)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Theme(content: @Composable () -> Unit)
{
    MaterialTheme(
        colors = lightColors(
            surface = Color(ChatColorsConfig.SURFACE),
            background = Color(ChatColorsConfig.TOP_GRADIENT.last()),
        ),
    ) {
        ProvideTextStyle(LocalTextStyle.current.copy(letterSpacing = 0.sp)) {
            content()
        }
    }
}
