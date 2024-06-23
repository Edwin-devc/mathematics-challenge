@extends('admin.layout.layout')
@section('content')
<div class="container-fluid">
    @if (session('success'))
    <div class="alert alert-success text-center alert-dismissible fade show" role="alert">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    @endif
    <!--  Row 1 -->
    <div class="card w-100">
        <div class="card-body p-4">
            <h5 class="card-title fw-semibold mb-4">Schools</h5>
            <button type="button" class="btn btn-secondary text-white btn-sm" data-bs-toggle="modal"
                data-bs-target="#exampleModal" data-bs-whatever="@mdo"><span><i class="ti ti-plus"></i></span>
                Upload Schools</button>

            {{-- form modal --}}
            <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">New Booking</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form method="POST" action="">
                                @csrf
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="name" name="name"
                                                placeholder="Name" required />
                                            <label for="name">Name</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="email" class="form-control" id="email" name="email"
                                                placeholder="Email" required />
                                            <label for="email">Email</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="contact" name="contact"
                                                placeholder="Contact" required />
                                            <label for="contact">Contact</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating date" id="date3" data-target-input="nearest">
                                            <input type="text" class="form-control datetimepicker-input" id="checkin"
                                                placeholder="Check In" name="check_in" data-target="#date3"
                                                data-toggle="datetimepicker" value="{{ session('check_in') }}"
                                                required />
                                            <label for="checkin">Check In</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating date" id="date4" data-target-input="nearest">
                                            <input type="text" class="form-control datetimepicker-input" id="checkout"
                                                placeholder="Check Out" name="check_out" data-target="#date4"
                                                data-toggle="datetimepicker" value="{{ session('check_out') }}"
                                                required />
                                            <label for="checkout">Check Out</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <select class="form-select" name="time" id="select1" required>
                                                <option value="Day" {{ session('time')=='Day' ? 'selected' : '' }}>
                                                    Day
                                                </option>
                                                <option value="Night" {{ session('time')=='Night' ? 'selected' : '' }}>
                                                    Night
                                                </option>
                                                <option value="Day & Night" {{ session('time')=='Day & Night'
                                                    ? 'selected' : '' }}>Day & Night
                                                </option>
                                            </select>
                                            <label for="select1">Select Time</label>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-floating">
                                            <select class="form-select" name="room" id="select3" required>
                                                <option value="1" {{ session('room')=='1' ? 'selected' : '' }}>1
                                                    Room
                                                </option>
                                                <option value="2" {{ session('room')=='2' ? 'selected' : '' }}>2
                                                    Rooms
                                                </option>
                                                <option value="3" {{ session('room')=='3' ? 'selected' : '' }}>3
                                                    Rooms
                                                </option>
                                                <option value="4" {{ session('room')=='4' ? 'selected' : '' }}>
                                                    4
                                                    Rooms
                                                </option>
                                                <option value="5">5 Rooms</option>
                                                <option value="6">6 Rooms</option>
                                                <option value="7">7 Room</option>
                                                <option value="8">8 Rooms</option>
                                                <option value="9">9 Rooms</option>
                                                <option value="10">10 Rooms</option>
                                                <option value="11">11 Rooms</option>
                                                <option value="12">12 Rooms</option>
                                            </select>
                                            <label for="select3">Number of Rooms</label>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-floating">
                                            <textarea class="form-control" placeholder="Special Request" name="request"
                                                id="message" style="height: 100px"></textarea>
                                            <label for="message">Special Request</label>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-danger btn-sm"
                                            data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn btn-success btn-sm">Book Now</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end form modal --}}

            {{-- view model --}}
            <!-- Button trigger modal -->
            {{-- <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal2">
                Launch demo modal
            </button> --}}

            <div class="table-responsive">
                <table class="table text-nowrap mb-0 align-middle">
                    <thead class="text-dark fs-4">
                        <tr>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Name</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">District</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Registration Number</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Representative Email</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Representative Name</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Status</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0 text-center">Action</h6>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        {{-- @if (count($bookings) === 0)
                        <tr class="text-center">
                            <td class="border-bottom-0" colspan="11">
                                <h6 class="fw-normal mb-0">No bookings found</h6>
                            </td>
                        </tr>
                        @endif
                        @foreach ($bookings as $booking)
                        <tr>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $booking->name }}</h6>
                        </td>
                        <td class="border-bottom-0">
                            <h6 class="fw-normal mb-0">{{ $booking->contact }}</h6>
                        </td>
                        <td class="border-bottom-0">
                            <p class="mb-0 fw-normal">{{ $booking->email }}</p>
                        </td>
                        <td class="border-bottom-0">
                            <p class="fw-normal mb-0">{{ $booking->check_in }}</p>
                        </td>
                        <td class="border-bottom-0">
                            <p class="fw-normal mb-0">{{ $booking->check_out }}</p>
                        </td>
                        <td class="border-bottom-0">
                            <p class="fw-normal mb-0">{{ $booking->time }}</p>
                        </td>
                        <td class="border-bottom-0">
                            <p class="fw-normal mb-0">{{ $booking->room }}</p>
                        </td>
                        <td class="border-bottom-0">
                            @if (!empty($booking->request))
                            <p class="fw-normal mb-0">{{ $booking->request }}</p>
                            @else
                            <p class="fw-normal mb-0 text-danger">N/A</p>
                            @endif
                        </td>
                        <td class="border-bottom-0">
                            <p class="fw-normal mb-0">{{ $booking->created_at }}</p>
                        </td>
                        <td class="border-bottom-0">
                            @if ($booking->status === 'Open')
                            <p class="btn btn-sm btn-danger fw-normal mb-0">{{ $booking->status }}</p>
                            @else
                            <p class="btn btn-sm btn-success fw-normal mb-0">{{ $booking->status }}</p>
                            @endif
                        </td>

                        <td class="border-bottom-0">
                            <p class="fw-bold mb-0 fs-4">
                                <span class="mx-2 text-success" data-bs-toggle="modal"
                                    data-bs-target="#exampleModalview{{ $booking->booking_id }}">
                                    <i class="ti ti-eye"></i>
                                </span>

                                <span class="me-2 text-primary" data-bs-toggle="modal"
                                    data-bs-target="#exampleModaledit{{ $booking->booking_id }}"
                                    data-bs-whatever="@mdo">
                                    <i class="ti ti-edit"></i>
                                </span>

                                <span class="me-2 text-primary" data-bs-toggle="modal"
                                    data-bs-target="#exampleModaldelete{{ $booking->booking_id }}"
                                    data-bs-whatever="@mdo">
                                    <i class="ti ti-trash text-danger"></i>
                                </span>

                                @if ($booking->status === 'Open')
                                <span class="ms-2 fw-bold mb-0 fs-7 text-success text-center" data-bs-toggle="modal"
                                    data-bs-target="#exampleModalcomplete{{ $booking->booking_id }}"
                                    data-bs-whatever="@mdo">
                                    <i class="ti ti-check"></i>
                                </span>
                                @endif

                            </p>
                        </td> --}}

                        <!-- view Modal -->
                        <div class="modal fade" id="exampleModalview" tabindex="-1" aria-labelledby="exampleModalLabel"
                            aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="exampleModalLabel">Booking Details
                                        </h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    {{-- <div class="modal-body">
                                            <p><span class="text-success">Name: </span>{{ $booking->name }}</p>
                                    <p><span class="text-success">Contact: </span>{{ $booking->contact }}
                                    </p>
                                    <p><span class="text-success">Email: </span>{{ $booking->email }}</p>
                                    <p><span class="text-success">Check In:
                                        </span>{{ $booking->check_in }}</p>
                                    <p><span class="text-success">Check Out:
                                        </span>{{ $booking->check_out }}</p>
                                    <p><span class="text-success">Time: </span>{{ $booking->time }}</p>
                                    <p><span class="text-success">Rooms: </span>{{ $booking->room }}</p>
                                    <p><span class="text-success">Request: </span>{{ $booking->request }}
                                    <p><span class="text-success">Booked On:
                                        </span>{{ $booking->created_at }}
                                    </p>

                                </div> --}}
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-danger btn-sm"
                                        data-bs-dismiss="modal">Close</button>
                                </div>
                            </div>
                        </div>
            </div>
            {{-- end view model --}}


            {{-- edit form modal --}}
            <div class="modal fade" id="exampleModaledit" tabindex="-1" aria-labelledby="exampleModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">Update Booking
                            </h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form method="POST" action="">
                                @csrf
                                @method('PUT')
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="name" name="name"
                                                placeholder="Name" value="" required />
                                            <label for="name">Name</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="email" class="form-control" id="email" name="email"
                                                placeholder="Email" value="" required />
                                            <label for="email">Email</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="contact" name="contact" value=""
                                                placeholder="Contact" required />
                                            <label for="contact">Contact</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating date" id="date3" data-target-input="nearest">
                                            <input type="text" class="form-control datetimepicker-input" id="checkin"
                                                placeholder="Check In" name="check_in" data-target="#date3"
                                                data-toggle="datetimepicker" value="" required />
                                            <label for="checkin">Check In</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating date" id="date4" data-target-input="nearest">
                                            <input type="text" class="form-control datetimepicker-input" id="checkout"
                                                placeholder="Check Out" name="check_out" data-target="#date4"
                                                data-toggle="datetimepicker" value="" required />
                                            <label for="checkout">Check Out</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            {{-- <select class="form-select" name="time" id="select1"
                                                                required>
                                                                <option value="Day" {{ $booking->time == 'Day' ?
                                                                    'selected' : '' }}>
                                            Day
                                            </option>
                                            <option value="Night" {{ $booking->time == 'Night' ?
                                                                    'selected' : '' }}>
                                                Night
                                            </option>
                                            <option value="Day & Night" {{ $booking->time == 'Day &
                                                                    Night' ? 'selected' : '' }}>
                                                Day & Night
                                            </option>
                                            </select> --}}
                                            <label for="select1">Select Time</label>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <div class="form-floating">
                                            <select class="form-select" name="room" id="select3" required>
                                                {{-- <option value="1" {{ $booking->room === '1' ?
                                                                    'selected' : '' }}>
                                                1 Room
                                                </option>
                                                <option value="2" {{ $booking->room === '2' ? 'selected'
                                                                    : '' }}>
                                                    2 Rooms
                                                </option>
                                                <option value="3" {{ $booking->room === '3' ? 'selected'
                                                                    : '' }}>
                                                    3 Rooms
                                                </option>
                                                <option value="4" {{ $booking->room === '4' ? 'selected'
                                                                    : '' }}>
                                                    4 Rooms
                                                </option>
                                                <option value="5" {{ $booking->room === '5' ? 'selected'
                                                                    : '' }}>
                                                    5 Rooms
                                                </option>
                                                <option value="6" {{ $booking->room === '6' ? 'selected'
                                                                    : '' }}>
                                                    6 Rooms
                                                </option>
                                                <option value="7" {{ $booking->room === '7' ? 'selected'
                                                                    : '' }}>
                                                    7 Rooms
                                                </option>
                                                <option value="8" {{ $booking->room === '8' ? 'selected'
                                                                    : '' }}>
                                                    8 Rooms
                                                </option>
                                                <option value="9" {{ $booking->room === '9' ? 'selected'
                                                                    : '' }}>
                                                    9 Rooms
                                                </option>
                                                <option value="10" {{ $booking->room === '10' ?
                                                                    'selected' : '' }}>
                                                    10 Rooms
                                                </option>
                                                <option value="11" {{ $booking->room === '11' ?
                                                                    'selected' : '' }}>
                                                    11 Rooms
                                                </option>
                                                <option value="12" {{ $booking->room === '12' ?
                                                                    'selected' : '' }}>
                                                    12 Rooms
                                                </option> --}}
                                            </select>
                                            <label for="select3">Number of Rooms</label>
                                        </div>
                                    </div>



                                    <div class="col-12">
                                        <div class="form-floating">
                                            <textarea class="form-control" placeholder="Special Request" name="request"
                                                id="message" style="height: 100px"></textarea>
                                            <label for="message">Special Request</label>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-danger btn-sm"
                                            data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn btn-success btn-sm">Update</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end edit form modal --}}

            {{-- delete modal --}}
            <div class="modal fade" id="exampleModaldelete" tabindex="-1" aria-labelledby="exampleModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">Delete Booking
                            </h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Are you sure you want to delete this booking?</p>
                            <form action="" method="POST" style="display:inline;" class="delete-form">
                                @csrf
                                @method('DELETE')
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-sm btn-success"
                                        data-bs-dismiss="modal">No</button>
                                    <button type="submit" class="btn btn-sm btn-danger">Yes</button>
                                </div>

                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end delete modal --}}

            {{-- mark complete modal --}}
            <div class="modal fade" id="exampleModalcomplete" tabindex="-1" aria-labelledby="exampleModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">Mark Complete
                            </h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Are you sure you want to mark this booking as complete?</p>
                            <form action="" method="POST" style="display:inline;" class="delete-form">
                                @csrf
                                @method('PUT')
                                <input type="hidden" name="status" value="Closed">
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-sm btn-success"
                                        data-bs-dismiss="modal">No</button>
                                    <button type="submit" class="btn btn-sm btn-danger">Yes</button>
                                </div>

                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end mark complete modal --}}
            </tr>
            {{-- @endforeach --}}

            </tbody>
            </table>
        </div>
    </div>
</div>

<!-- end Row 1 -->


{{-- <div class="card">
        <div class="card-body">
            <h5 class="card-title fw-semibold mb-4">Bookings</h5>
            <div class="card">
                <div class="card-body p-4">

                </div>
            </div>
            <div class="card">
                <div class="card-body p-4">
                    <button type="button" class="btn btn-primary m-1">Primary</button>
                    <button type="button" class="btn btn-secondary m-1">Secondary</button>
                    <button type="button" class="btn btn-success m-1">Success</button>
                    <button type="button" class="btn btn-danger m-1">Danger</button>
                    <button type="button" class="btn btn-warning m-1">Warning</button>
                    <button type="button" class="btn btn-info m-1">Info</button>
                    <button type="button" class="btn btn-light m-1">Light</button>
                    <button type="button" class="btn btn-dark m-1">Dark</button>
                    <button type="button" class="btn btn-link m-1">Link</button>
                </div>
            </div>
            <h5 class="card-title fw-semibold mb-4">Outline buttons</h5>
            <div class="card mb-0">
                <div class="card-body p-4">
                    <button type="button" class="btn btn-outline-primary m-1">Primary</button>
                    <button type="button" class="btn btn-outline-secondary m-1">Secondary</button>
                    <button type="button" class="btn btn-outline-success m-1">Success</button>
                    <button type="button" class="btn btn-outline-danger m-1">Danger</button>
                    <button type="button" class="btn btn-outline-warning m-1">Warning</button>
                    <button type="button" class="btn btn-outline-info m-1">Info</button>
                    <button type="button" class="btn btn-outline-light m-1">Light</button>
                    <button type="button" class="btn btn-outline-dark m-1">Dark</button>
                    <button type="button" class="btn btn-outline-link m-1">Link</button>
                </div>
            </div>
        </div>
    </div> --}}

</div>
@endsection