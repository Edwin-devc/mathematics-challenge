<!DOCTYPE html>
<html>
<head>
    <title>End of Challenge Report</title>
</head>
<body>
    <h1>End of Challenge Report</h1>
    <p>Hello {{ $participant->name }},</p>
    <p>Thank you for participating in our challenge. Please find the questions and answers below:</p>

    @foreach($questions as $question)
        <h3>{{ $question->text }}</h3>
        <ul>
            @foreach($question->answers as $answer)
                <li>{{ $answer->text }}</li>
            @endforeach
        </ul>
    @endforeach

    <p>Thanks,</p>
    <p>{{ config('app.name') }}</p>
</body>
</html>
