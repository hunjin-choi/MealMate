<!DOCTYPE html>
<html>
<head>
    <title>Mealmate List</title>
</head>
<body>
<h1>Mealmate List</h1>
<table>
    <tr>
        <th>Mileage</th>
        <th>Giver</th>
        <th>ConnectDate</th>
        <th>ExpectedDisconnectDate</th>
        <th>ActualDisconnectDate</th>
        <th>Actions</th>
    </tr>
    <#list mealmateList as item>
        <tr>
            <td>${item.mileagePerMealmate}</td>
            <td>${item.giverId}</td>
            <td>${item.connectDate?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>${item.expectedDisconnectDate?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>
                <#if item.actualDisconnectDate??>
                    ${item.actualDisconnectDate?string("yyyy-MM-dd HH:mm:ss")}
                <#else>
                    N/A
                </#if>
            </td>
            <td><a href="/mealmate/feedback/history/${item.mealmateId}">View Feedback History</a></td>
        </tr>
    </#list>
</table>
</body>
</html>