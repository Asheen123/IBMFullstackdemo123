'Create Connection Object of OTA API
Dim qcConn
dim objExcel
dim objWorkbook

dim fso,folder,file
Set qcConn = CreateObject("TDApiOle80.TDConnection")
dim theTestCase, myTestSet
sub testALMConnection(tstSetID,tcList,folderPath)
testSetID = WScript.Arguments.Item(0)
tcList = WScript.Arguments.Item(1)
strScriptPath = WScript.Arguments.Item(2)
almURL = WScript.Arguments.Item(3)
userName = WScript.Arguments.Item(4)
password = WScript.Arguments.Item(5)
domain = WScript.Arguments.Item(6)
project = WScript.Arguments.Item(7)
testrunupdate = WScript.Arguments.Item(8)
tcaseArr = split(tcList,",")
	
retVal = connectToALM(almURL,userName,password,domain,project)
if retVal = True then
	strMessage = strMessage &"<br> ALM Connection Successful...."
	''msgbox "Connected To ALM"
end if

for intRow= 0 to ubound(tcaseArr) step 1
	
		tcNumber = tcaseArr(intRow)
		tcStatus =split(tcNumber,":")
		updateTestCaseWithStatus testSetID,tcStatus(0),tcStatus(1),strScriptPath,testrunupdate
If testrunupdate <> "yes" then
		dim arrResults
		arrResults = getFilesFromFolder(strScriptPath,tcStatus(0))
		counter = 0
		for each fil in arrResults
		If fil <> "" Then
		resPath = strScriptPath+"\"+fil
		counter = counter + 1
		''msgbox "RESPATH"&resPath		
		addAttachmentToALM theTestCase,resPath	
		end if
		next
end if		
next
On Error Resume Next
If err.number <> 0 then   
    err.clear
End If
''msgbox "Upload Completed. Please check ALM test set for results !"

if qcConn.ProjectConnected then
qcConn.Disconnect
end if

if qcConn.LoggedIn then
qcConn.Logout
end if

 if qcConn.Connected then
 qcConn.ReleaseConnection
 end if
 
end sub

public function getFilesFromFolder(folderPath,searchString)
Set fso = CreateObject("Scripting.FileSystemObject")  
Set folder = fso.GetFolder(folderPath)
numFiles = 0
numFiles = folder.Files.Count
dim resArray()
e = 0
redim resArray(numFiles)
for each file in folder.Files
if instr(file.name,searchString) > 0 then
''msgbox file.name
redim preserve resArray(e+1)
resArray(e) = file.name 
		e = e + 1
		
end if
next
getFilesFromFolder= resArray
end function

Public Function connectToALM(url, userName, password, domain, Project) 
qcConn.InitConnectionEx URL
qcConn.Login UserName, Password
qcConn.Connect Domain, Project
 If qcConn.Connected = False or qcConn.LoggedIn = False or qcConn.ProjectConnected = False Then
 ErrorString = "QC Connect failed!!!"
 ConnectALM = False
  Exit Function
 else
 connectToALM = True
end if
End function

public function updateTestCaseWithStatus(testSetID, testCaseID, testStatus,strScriptPath,testrunupdate)
	Set tSetFactory = qcConn.TestSetFactory
	Set tSetFilter = tSetFactory.Filter
	tSetFilter.Filter("CY_CYCLE_ID") = testSetID	
	Set tstSetList = tSetFilter.NewList
	Set myTestSet = tstSetList.Item(1)
	Set tsTestFactory = myTestSet.TSTestFactory
	Set testInstanceFilter = tsTestFactory.Filter
	testInstanceFilter.Filter("TS_TEST_ID") = testCaseID
	''msgbox testCaseID
	Set tsTestList = tsTestFactory.NewList(testInstanceFilter.Text)
	Set theTestCase = tsTestList.Item(1)
	''msgbox theTestCase.ID
	Set runFactory = theTestCase.RunFactory
	Set runObject = runFactory.AddItem("Auto_Run")
	runObject.Status = testStatus
	runObject.Post
If testrunupdate = "yes" then
       dim arrResults
		arrResults = getFilesFromFolder(strScriptPath,testCaseID)
		for each fil in arrResults
		If fil <> "" Then
		resPath = strScriptPath+"\"+fil
		counter = counter + 1
		'msgbox "RESPATH"&resPath		
		addAttachmentToALM runObject,resPath	
		end if
		next
end if
end function


Public Function addAttachmentToALM(tsTest, attachmentFilePath)
''msgbox attachmentFilePath
Set attachmentFactory = tsTest.Attachments
Set theAttachment = attachmentFactory.AddItem(null)
theAttachment.FileName = attachmentFilePath
theAttachment.Type = 1
theAttachment.Post
end function

Public Function disconnectfromALM()
if qcConn.ProjectConnected then
qcConn.Disconnect
end if
if qcConn.LoggedIn then
qcConn.Logout
end if
 if qcConn.Connected then
 qcConn.ReleaseConnection
 end if
 disconnectfromALM = true
end function

	testALMConnection tstSetID,tcList,folderPath
	