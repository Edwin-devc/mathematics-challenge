<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasOne;

class School extends Model
{
    use HasFactory;

    protected $table = 'schools';
    protected $primaryKey = 'school_id';
    public $incrementing = false;
    protected $keyType = 'string';
    
    protected $fillable = [
        'school_id',
        'name',
        'district',
        'registration_number',
        'representative_email',
        'representative_name'
    ];
    
    public function representative(): HasOne
    {
        return $this->hasOne(Representative::class, 'school_id');
    }
}