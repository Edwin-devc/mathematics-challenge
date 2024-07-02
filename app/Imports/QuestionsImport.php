<?php

namespace App\Imports;

use App\Models\Question;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
class QuestionsImport implements ToModel, WithHeadingRow
{
    protected $challenge_id;

    public function __construct($challenge_id)
    {
        $this->challenge_id = $challenge_id;
    }
    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        return new Question([
            'text' => $row['question'],
            'marks' => $row['marks'],
            'challenge_id' => $this->challenge_id
        ]);
    }
}
