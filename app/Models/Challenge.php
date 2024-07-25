<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Challenge extends Model
{
    use HasFactory;
    protected $table = 'challenges';
    protected $primaryKey = 'challenge_id';
    public $incrementing = true;

    protected $fillable = [
        'name',
        'start_date',
        'end_date',
        'duration',
        'number_of_questions'
    ];

    public function questions(): HasMany
    {
        return $this->hasMany(Question::class, 'challenge_id');
    }
    public function answers(): HasMany
    {
        return $this->hasMany(Answer::class, 'challenge_id');
    }

    public function attempts(): HasMany
    {
        return $this->hasMany(Attempt::class, 'challenge_id');
    }
}
