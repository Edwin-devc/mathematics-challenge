<?php

namespace App\Imports;

use App\Models\Answer;
use App\Models\Question;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Illuminate\Support\Facades\Log;

class AnswersImport implements ToModel, WithHeadingRow
{
    protected $questions;

    public function __construct($challenge_id)
    {
        // Fetch questions associated with the given challenge_id
        $this->questions = Question::where('challenge_id', $challenge_id)->orderBy('challenge_id')->get();
        Log::info('Questions fetched for challenge_id ' . $challenge_id, ['questions' => $this->questions]);
    }

    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        // Check if there are still questions available
        if ($this->questions->isEmpty()) {
            Log::error('No questions available for the provided challenge_id.');
            return null;
        }

        // Shift the first question from the collection
        $question = $this->questions->shift();
        Log::info('Question associated with row', ['question' => $question]);

        return new Answer([
            'challenge_id' => $question->challenge_id,
            'question_id' => $question->question_id,
            'answer' => $row['answer'],
        ]);
    }
}
