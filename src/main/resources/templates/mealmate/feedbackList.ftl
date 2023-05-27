<!DOCTYPE html>
<html>
<head>
    <title>Feedback History</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <h1 class="mt-5 mb-3">Feedback History</h1>
    <table class="table table-bordered table-striped">
        <thead class="thead-dark">
        <tr>
            <th>Mileage Per Feedback</th>
            <th>Feedback Mention</th>
            <th>Feedback Date</th>
        </tr>
        </thead>
        <tbody>
        <#list feedbackList as item>
            <tr>
                <td>${item.mileagePerFeedback}</td>
                <td>
                    <#if item.feedbackMention??>
                        ${item.feedbackMention}
                    <#else>
                        N/A
                    </#if>
                </td>
                <td>${item.feedBackDate?string("yyyy-MM-dd HH:mm:ss")}</td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>
