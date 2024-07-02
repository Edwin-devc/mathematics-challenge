<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>welcome email</title>
</head>
<body>
    <p>Hey {{ $representative->name }}, you have been added as the representative for {{ $school_name }}.</p>
    <p>Your login password is: {{$representative->password}}</p>
</body>
</html>