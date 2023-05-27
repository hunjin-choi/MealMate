<!DOCTYPE html>
<html>
<head>
    <title>Mileage History</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <h1 class="mt-5 mb-3">Mileage History</h1>
    <table class="table table-bordered table-striped">
        <thead class="thead-dark">
        <tr>
            <th>Mileage</th>
            <th>Reason</th>
            <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <#list mileageHistoryList as item>
            <tr>
                <td>${item.mileage.currentMileage}</td>
                <td>${item.mileageChangeReason}</td>
                <td>${item.date?string("yyyy-MM-dd HH:mm:ss")}</td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>
