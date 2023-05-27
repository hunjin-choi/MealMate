<!DOCTYPE html>
<html>
<head>
    <title>Mealmate List</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <h1 class="mt-5 mb-3">Mealmate List</h1>
    <table class="table table-bordered table-striped">
        <thead class="thead-dark">
        <tr>
            <th>Mileage</th>
            <th>Giver</th>
            <th>Connect Date</th>
            <th>Expected Disconnect Date</th>
            <th>Actual Disconnect Date</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <#list mealmateList as item>
        <tr>
            <td>${item.mileagePerMealmate}</td>
            <td>${item.giverId}</td>
            <td>${item.connectDate?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>${item.expectedDisconnectDate?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>
                <#if item.actualDisconnectDate??>
${item.actualDisconnectDate?string("yyyy-MM-dd
