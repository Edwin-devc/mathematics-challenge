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
    public $incrementing = false;
    protected $keyType = 'string';

    protected $fillable = [
        'challenge_id',
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
}
