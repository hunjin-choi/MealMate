<!DOCTYPE html>
<html>
<head>
    <title>Chat Period List</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <h1 class="mt-5 mb-3">Chat Period List</h1>
    <table class="table table-bordered table-striped">
        <thead class="thead-dark">
        <tr>
            <th>Start Time</th>
            <th>End Time</th>
        </tr>
        </thead>
        <tbody>
        <#list chatPeriodList as item>
            <tr>
                <td>${item.startTime.hour}:${item.startTime.minutes}</td>
                <td>${item.endTime.hour}:${item.endTime.minutes}</td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>
