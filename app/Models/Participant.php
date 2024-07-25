<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use \Illuminate\Database\Eloquent\Relations\HasMany;

class Participant extends Model
{
    use HasFactory;
    protected $table = 'participants';
    protected $primaryKey = 'participant_id';

    protected $fillable = [
        'username',
        'firstname',
        'lastname',
        'email',
        'date_of_birth',
        'image_path',
        'password',
        'school_registration_number',
        'total_attempts',
        'total_challenges',
    ];

    public function attempts(): HasMany
    {
        return $this->hasMany(Attempt::class, 'participant_id');
    }
}
