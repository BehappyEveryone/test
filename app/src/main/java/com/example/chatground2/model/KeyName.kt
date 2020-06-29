package com.example.chatground2.model

object KeyName {
    //소켓 on
    const val socketOnConnect = "onConnect"
    const val socketOnDisconnect = "onDisconnect"
    const val socketMakeRoom = "makeRoom"
    const val socketMatchMaking = "matchMaking"
    const val socketMessage = "message"
    const val socketOfferSubject = "offerSubject"
    const val socketRoomInfoChange = "roomInfoChange"
    const val socketPresentationOrder = "presentationOrder"
    const val socketReVoting = "reVoting"
    const val socketResult = "result"

    //소켓 emit
    const val emitSendMessage = "sendMessage"
    const val emitLeaveRoom = "leaveRoom"
    const val emitOpinionResult = "opinionResult"
    const val emitReVoteResult = "reVoteResult"
    const val emitResultValue = "resultValue"
    const val emitOnMakeRoom = "onMakeRoom"
    const val emitStrategicTimeComplete = "strategicTimeComplete"
    const val emitStrategicTimeComplete2 = "strategicTimeComplete2"

    //키
    const val intentMessageValue = "messageValue"
    const val intentOfferSubjectValue = "offerSubjectValue"
    const val intentRoomInfoChangeValue = "roomInfoChangeValue"
    const val intentPresentationOrderValue = "presentationOrderValue"
    const val intentReVotingValue = "reVotingValue"
    const val intentResultValue = "resultValue"

    const val binaryText = "binaryData"
    const val typeText = "type"
    const val contentText = "content"
    const val roomText = "room"
    const val userText = "user"
    const val usersText = "users"
    const val joinText = "join"
    const val messageText = "message"
    const val reVoteText = "reVote"
    const val timeText = "time"
    const val opinionText = "opinion"
    const val subjectText = "subject"
    const val replyCommentIdText = "replyCommentId"
    const val winnerText = "winner"
    const val emailText = "email"
    const val passwordText = "password"
    const val introduceText = "introduce"
    const val profileText = "profile"
    const val pageText = "page"
    const val bestText = "best"
    const val searchText = "search"
    const val kindText = "kind"
    const val keywordText = "keyword"
    const val roomInfoText = "roomInfo"
    const val imageUrlText = "imageUrl"
    const val imageUploadName = "img"
    //value
    const val typeStrategicImageText = "strategicImage"
    const val typeStrategicVideoText = "strategicVideo"
    const val typeStrategicTextText = "strategicText"
    const val typeImageText = "image"
    const val typeVideoText = "video"
    const val typeTextText = "text"
    const val typeSystemText = "system"

    //게임
    const val neutrality = "neutrality"
    const val agree = "agree"
    const val oppose = "oppose"
    const val all = "all"

    //extra
    const val idxText = "idx"
    const val idText = "id"
    const val titleText = "title"
    const val imagePathText = "imagePath"
    const val imagePathArrayText = "imagePathArray"
    //shared key
    const val sharedChatGroundText = "ChatGround"
    const val userCapital = "User"
    const val autoBoolean = "Auto"
    const val autoEmail = "AutoEmail"
    const val autoPassword = "AutoPassword"
    const val sharedMessage = "message"

    const val agreeSign = "찬"
    const val opposeSign = "반"
    const val neutralitySign = "중"


    //path
    const val forumImageServerPath = "forumImages"
}