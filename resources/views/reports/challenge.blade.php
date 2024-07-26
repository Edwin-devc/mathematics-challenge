<!DOCTYPE html>
<html>
<head>
    <title>Challenge Report</title>
</head>
<body>
    <h1>{{ $challenge->title }}</h1>
    <p>{{ $challenge->description }}</p>

    <h2>Questions and Answers</h2>
    <ul>
        @foreach($questions as $question)
            <li>
                <strong>Question:</strong> {{ $question->text }} <br>
                <strong>Answer:</strong> {{ $question->answer }}
            </li>
        @endforeach
    </ul>
</body>
</html>
