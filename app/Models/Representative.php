<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Representative extends Model
{
    use HasFactory;
    protected $table = 'representatives';
    protected $primaryKey = 'representative_id';
    public $incrementing = false;
    protected $keyType = 'string';
    
    protected $fillable = [
        'representative_id',
        'name',
        'email',
        'password',
        'school_id'
    ];

    public function school(): BelongsTo
    {
        return $this->belongsTo(School::class, 'school_id');
    }
}
